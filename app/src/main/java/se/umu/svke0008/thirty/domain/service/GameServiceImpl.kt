package se.umu.svke0008.thirty.domain.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import se.umu.svke0008.thirty.GameSettingsConstants
import se.umu.svke0008.thirty.domain.model.Dice
import se.umu.svke0008.thirty.domain.model.GameState
import se.umu.svke0008.thirty.domain.model.ScoreChoice
import se.umu.svke0008.thirty.domain.model.ScoreHistory
import se.umu.svke0008.thirty.domain.model.saveLockedDice
import se.umu.svke0008.thirty.domain.utils.Combination
import se.umu.svke0008.thirty.domain.utils.DiceUtils
import se.umu.svke0008.thirty.domain.utils.ScoreUtils
import se.umu.svke0008.thirty.utils.UiUtils
import se.umu.svke0008.thirty.utils.UiUtils.toInt

/**
 * [GameServiceImpl] is an implementation of the [GameService] interface. It provides
 * various functionalities to control and manage the state of a game. It is capable of
 * maintaining the state of the game, throwing dice, selecting a die, locking a die,
 * calculating score, and much more. It exposes a number of flows to allow other parts
 * of the application to reactively observe changes in the game state.
 *
 * @property _gameState Mutable state flow holding the current game state.
 * @property scoreChoicesFlow Flow for list of score choices.
 * @property scoreHistoryFlow Flow for list of score histories.
 * @property diceListFlow Flow for list of dice.
 * @property _eventFlow Shared flow for game events.
 *
 * @property helpAssistEnabled Flow indicating whether help assist is enabled.
 * @property newGameFlow Flow indicating whether a new game has started.
 * @property roundsLeftFlow Flow indicating the number of rounds left.
 * @property throwsLeftFlow Flow indicating the number of throws left.
 * @property gameOver Flow indicating whether the game is over.
 * @property noMoreThrows Flow indicating whether there are no more throws left.
 * @property hasThrown Flow indicating whether the player has thrown.
 * @property endTurnEventFlow Shared flow for end turn events.
 * @property hasWon Flow indicating whether the player has won.
 *
 * @property currentSelectedChoice Flow indicating the currently selected score choice.
 * @property hasSelectedChoice Flow indicating whether a score choice has been selected.
 * @property endTurnAllowed Flow indicating whether the end turn action is allowed.
 * @property calculatedScore Flow indicating the calculated score.
 *
 */
class GameServiceImpl : GameService {

    private val _gameState = MutableStateFlow(createInitialGameState())
    private val scoreChoicesFlow: Flow<List<ScoreChoice>> = _gameState.map { it.scoreChoices }
        .distinctUntilChanged()
    private val scoreHistoryFlow: Flow<List<ScoreHistory>> = _gameState.map { it.scoreHistory }
        .distinctUntilChanged()
    private val diceListFlow: Flow<List<Dice>> = _gameState.map { it.diceList }
        .distinctUntilChanged()
    private val _eventFlow = MutableSharedFlow<String>()

    override val helpAssistEnabled = _gameState.map { it.helpAssist }.distinctUntilChanged()
    override val newGameFlow: Flow<Boolean> = _gameState.map { it.newGame }
    override val roundsLeftFlow: Flow<Int> = _gameState.map { it.roundsLeft }
    override val throwsLeftFlow: Flow<Int> = _gameState.map { it.throwsLeft }
    override val gameOver = roundsLeftFlow.map { it == 0 }
    override val noMoreThrows = throwsLeftFlow.map { it == 0 }
    override val hasThrown = throwsLeftFlow.map { it < GameSettingsConstants.THROWS_AMOUNT }
    override val endTurnEventFlow: SharedFlow<String> = _eventFlow
    override val hasWon: Flow<Boolean> =
        _gameState.map { it.totalScore >= GameSettingsConstants.WINNING_CONDITION }


    private val currentSelectedChoice = scoreChoicesFlow.map { scoreChoices ->
        scoreChoices.find { it.selected && !it.used }
    }
    override val hasSelectedChoice = currentSelectedChoice.map { it != null }

    override fun getGameState(): Flow<GameState> = _gameState.asStateFlow()

    override fun getTotalScore(): Flow<Int> = _gameState.map { it.totalScore }

    override fun getCalculatedScore(): Flow<Int> = calculatedScore

    override fun getScoreChoices(): Flow<List<ScoreChoice>> = scoreChoicesFlow

    override fun getScoreHistory(): Flow<List<ScoreHistory>> = scoreHistoryFlow

    /**
     * Checks if a "End turn" action is allowed based on whether the user has thrown all dice at least once
     * and has selected a scoring choice.
     *
     * @return [Flow] of [Boolean] representing whether the "End turn" action is allowed.
     */
    override val endTurnAllowed =
        combine(hasThrown, hasSelectedChoice) { hasThrown, hasSelectedChoice ->
            hasThrown && hasSelectedChoice
        }

    /**
     * Represents the calculated score for the currently selected scoring choice and locked dice combination.
     *
     * This is a combination of [diceListFlow] and [currentSelectedChoice]. For each new pair of values,
     * it filters the locked dice and calculates the valid combinations for the selected score choice.
     *
     * @return [Flow] of [Int] that emits the calculated score for each new pair of dice list and score choice.
     * If the current score choice is null, it emits 0.
     */
    private val calculatedScore =
        combine(diceListFlow, currentSelectedChoice) { diceList, scoreChoice ->
            scoreChoice?.let {
                val selectedDice = diceList.filter { it.locked }
                DiceUtils.calculatedValidCombinations(selectedDice, scoreChoice.score)
            } ?: 0
        }

    /**
     * Generates all valid dice combinations based on the current dice state and the currently
     * selected score choice, only when the help assistant feature is enabled.
     * If [helpAssistEnabled] is disabled or if no score choice is selected, an empty list is returned.
     *
     * @return [Flow] of a [List] of [Combination]s representing all valid combinations or an empty list.
     */
    private val automatedCombinations =
        combine(diceListFlow, currentSelectedChoice, helpAssistEnabled)
        { diceList, scoreChoice, helpAssist ->
            if (helpAssist) {
                scoreChoice?.let {
                    DiceUtils.findLargestAmountOfValidCombinations(it.score, diceList)
                } ?: emptyList()
            } else {
                emptyList()
            }
        }


    /**
     * Retrieves all dice in their current state.
     * If [helpAssistEnabled] is enabled, the dice will be colored accordingly
     * to any valid combinations.
     *
     * @return [Flow] of a [List] of [Dice] objects representing all dice.
     */
    override fun getDice(): Flow<List<Dice>> =
        combine(diceListFlow, automatedCombinations) { diceList, combinations ->
            colorCombinationDice(diceList, combinations)
        }

    /**
     * Rolls all dice that are not locked, assigning them each a new random number.
     * This function also updates the game state, reducing the number of throws left and
     * updating the state of the dice.
     * Any color assigned to a dice from a previous combination is reset in this process. This
     * enables new colors to be assigned if the dice become part of a new combination after the roll.
     *
     * @param diceList The current state of all dice in the game.
     * @return [Unit].
     */
    override fun throwDice(diceList: List<Dice>) {
        updateGameState {
            copy(
                diceList = diceList.map { die ->
                    if (!die.saveLockedDice()) {
                        val random = DiceUtils.getRandomDiceNumber()
                        die.copy(amount = random, colorInt = null)
                    } else {
                        die.copy(colorInt = null)
                    }
                },
                throwsLeft = throwsLeft - 1
            )
        }
    }

    /**
     * Select a specific [die] from the current state of the dice, altering its state within the game.
     * If the die is currently selected, it will be deselected and unlocked.
     * If it's not selected, it will be marked as selected. All other dice remain unchanged.
     *
     * @param die The die to be selected or deselected.
     * @param diceList The current state of all dice in the game.
     * @return [Unit].
     */
    override fun selectDice(die: Dice, diceList: List<Dice>) {
        updateGameState {
            copy(
                diceList = diceList.map { currentDie ->
                    if (currentDie.id == die.id) {
                        when {
                            currentDie.selected -> currentDie.copy(
                                locked = false,
                                selected = false
                            )

                            else -> {
                                currentDie.copy(selected = true)
                            }
                        }
                    } else {
                        currentDie
                    }
                }
            )
        }
    }

    /**
     * Lock or unlock a specific [die] from the current state of the dice, altering its state
     * within the game. If the die is currently selected, it will be deselected and unlocked.
     * If it's not selected, its locked state will be toggled. All other dice remain unchanged.
     *
     * @param die The die to be locked or unlocked.
     * @param diceList The current state of all dice in the game.
     * @return [Unit].
     */
    override fun lockDice(die: Dice, diceList: List<Dice>) {
        updateGameState {
            copy(
                diceList = diceList.map { currentDie ->
                    if (currentDie.id == die.id) {
                        when {
                            currentDie.selected -> currentDie.copy(
                                locked = false,
                                selected = false
                            )

                            else -> currentDie.copy(locked = !currentDie.locked)
                        }
                    } else {
                        currentDie
                    }
                }
            )
        }
    }


    /**
     * Selects a specific [ScoreChoice] from the current state of the game, altering its selected state.
     * If the score choice is already selected, it will be deselected.
     * If it's not selected, it will be marked as selected, and all other unused ScoreChoices
     * will be deselected.
     * After a score choice selection, the color of all dice is reset.
     *
     * @param selectedScoreChoice The score choice to be selected or deselected.
     * @return [Unit].
     */
    override fun selectScoreChoice(selectedScoreChoice: ScoreChoice) {
        updateGameState {
            copy(
                scoreChoices = scoreChoices.map { scoreChoice ->
                    when {
                        scoreChoice.id == selectedScoreChoice.id ->
                            scoreChoice.copy(selected = !scoreChoice.selected)

                        !scoreChoice.used -> scoreChoice.copy(selected = false)
                        else -> scoreChoice
                    }
                },
                diceList = diceList.map { die -> die.copy(colorInt = null) }
            )
        }
    }

    /**
     * End the current turn, performing several actions in sequence.
     * First, it retrieves the score and selected choice for this turn.
     * It then adds the score to the history and updates the total score.
     * It marks the selected choice as used, updates the game state, and resets all dice for the next turn.
     * If the game is not yet over, it emits a snack-bar message accordingly with the score.
     * If the game is over, it emits an empty string.
     *
     *
     * @return [Unit].
     */
    override suspend fun endTurn() {
        val score = calculatedScore.first()
        val choice = currentSelectedChoice.first()

        addScoreToHistory(choice, score)
        updateScore(score)

        useScoreChoice()
        updateGameState()
        resetAllDice()

        if (!gameOver.first()) {
            _eventFlow.emit(ScoreUtils.getSnackBarMessage(score))
        } else {
            _eventFlow.emit("")
        }
    }

    /**
     * Starts a new game by resetting the game state to a new game state using the
     * [createStartNewGameState] function. The newly created game state is then assigned
     * to the [_gameState] MutableStateFlow object.
     *
     * @return [Unit].
     */
    override fun startNewGame() {
        _gameState.value = createStartNewGameState()
    }

    /**
     * Toggles the help assistant feature. If it's currently off, it will be turned on and vice versa.
     * This also updates the dice color accordingly, resetting them if help assist is off.
     *
     * @return [Unit].
     */
    override fun toggleHelpAssist() {
        val newHelpAssist = !_gameState.value.helpAssist
        val newPlainDice = if (newHelpAssist) {
            _gameState.value.diceList
        } else {
            _gameState.value.diceList.map { die -> die.copy(colorInt = null) }
        }
        updateGameState {
            copy(helpAssist = newHelpAssist, diceList = newPlainDice)
        }
    }

    /**
     * Retrieves and sets a saved game state.
     *
     * @param gameState The saved game state to be retrieved.
     * @return [Unit].
     */
    override fun retrieveSavedGameState(gameState: GameState) {
        _gameState.value = gameState
    }

    /**
     * Adds a score to the score history. Throws an [IllegalArgumentException] if no score choice is selected.
     *
     * @param scoreChoice The score choice that the user has selected.
     * @param calculatedScore The calculated score based on the selected choice.
     * @return [Unit].
     */
    private fun addScoreToHistory(scoreChoice: ScoreChoice?, calculatedScore: Int) {
        if (scoreChoice != null) {
            updateGameState {
                copy(scoreHistory = scoreHistory + ScoreHistory(scoreChoice.text, calculatedScore))
            }
        } else {
            throw IllegalArgumentException("User has to select a score-choice first!")
        }
    }

    /**
     * Updates the total score.
     *
     * @param score The score to be added to the total score.
     * @return [Unit].
     */
    private fun updateScore(score: Int) {
        updateGameState {
            copy(totalScore = score + this.totalScore)
        }
    }

    /**
     * Marks the selected score choice as used.
     *
     * @return [Unit].
     */
    private fun useScoreChoice() {
        val updatedScoreChoices = _gameState.value.scoreChoices.map { scoreChoice ->
            if (scoreChoice.selected) {
                scoreChoice.copy(used = true, selected = false)
            } else {
                scoreChoice
            }
        }
        updateGameState {
            copy(scoreChoices = updatedScoreChoices)
        }
    }

    /**
     * Updates the game state, decrementing the rounds left by 1 and resetting the number of throws left to the
     * default throws amount.
     *
     * @return [Unit].
     */
    private fun updateGameState() {
        updateGameState {
            copy(
                roundsLeft = roundsLeft - 1,
                throwsLeft = GameSettingsConstants.THROWS_AMOUNT
            )
        }
    }

    /**
     * Resets all state of the dice.
     *
     * @return [Unit].
     */
    private fun resetAllDice() {
        val resetDice = _gameState.value.diceList.map { die ->
            die.copy(selected = false, locked = false, colorInt = null)
        }
        updateGameState {
            copy(diceList = resetDice)
        }
    }

    /**
     * Colors the dice that are part of valid combinations. If there are no valid combinations, the original
     * dice list are returned.
     *
     * @param diceList The list of dice to be potentially colored.
     * @param combinations The list of valid combinations.
     * @return A list of dice, potentially with updated colors.
     */
    private fun colorCombinationDice(
        diceList: List<Dice>,
        combinations: List<Combination>
    ): List<Dice> {
        if (combinations.isEmpty()) {
            return diceList
        }

        var coloredDice = diceList.map { it.copy() }.toMutableList()

        combinations.forEach { combination ->
            val color = UiUtils.getRandomColor()
            coloredDice = coloredDice.map { die ->
                if (combination.any { it.id == die.id } && die.colorInt == null) {
                    die.copy(colorInt = color.toInt())
                } else die
            }.toMutableList()
        }

        return coloredDice
    }

    /**
     * Creates an initial game state for a new game. The game state will have a false newGame flag,
     * false helpAssist, a score of 0, rounds and throws left set to [GameSettingsConstants],
     * score choices generated from the [ScoreUtils.choiceGenerator], empty score history and plain dice.
     *
     * @return [GameState] instance representing the initial state of a new game.
     */
    private fun createInitialGameState(): GameState {
        return GameState(
            newGame = false,
            helpAssist = false,
            totalScore = 0,
            roundsLeft = GameSettingsConstants.ROUNDS_AMOUNT,
            throwsLeft = GameSettingsConstants.THROWS_AMOUNT,
            scoreChoices = ScoreUtils.choiceGenerator(),
            scoreHistory = emptyList(),
            diceList = DiceUtils.createDice(GameSettingsConstants.DICE_AMOUNT),
        )
    }

    /**
     * Creates a game state for starting a new game. The game state will have a true newGame flag,
     * the helpAssist value from the previous game, a score of 0, rounds and throws left set to [GameSettingsConstants],
     * score choices generated from the [ScoreUtils.choiceGenerator], empty score history and plain dice.
     *
     * @return [GameState] instance representing the state of a new game.
     */
    private fun createStartNewGameState(): GameState {
        return GameState(
            newGame = true,
            helpAssist = _gameState.value.helpAssist,
            totalScore = 0,
            roundsLeft = GameSettingsConstants.ROUNDS_AMOUNT,
            throwsLeft = GameSettingsConstants.THROWS_AMOUNT,
            scoreChoices = ScoreUtils.choiceGenerator(),
            scoreHistory = emptyList(),
            diceList = DiceUtils.createDice(GameSettingsConstants.DICE_AMOUNT),
        )
    }

    /**
     * Updates the game state using a provided helper function.
     *
     * @param block Function that describes how to update the game state. This function should be
     *              defined in a way that it receives a GameState object and returns a GameState object.
     * @return [Unit].
     */
    private fun updateGameState(block: GameState.() -> GameState) {
        _gameState.value = block(_gameState.value)
    }

}