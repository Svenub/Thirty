package se.umu.svke0008.thirty.domain.service

import se.umu.svke0008.thirty.domain.model.Dice
import se.umu.svke0008.thirty.domain.model.GameState
import se.umu.svke0008.thirty.domain.model.ScoreChoice
import se.umu.svke0008.thirty.domain.model.ScoreHistory
import kotlinx.coroutines.flow.Flow

/**
 * Interface that defines operations for the game-service.
 */
interface GameService {

    /**
     * Retrieves the current state of the game.
     */
    fun getGameState(): Flow<GameState>

    /**
     * Retrieves the current list of dice.
     */
    fun getDice(): Flow<List<Dice>>

    /**
     * Performs a dice throw action using the given list of dice.
     */
    fun throwDice(diceList: List<Dice>)

    /**
     * Selects a die from the given list of dice.
     */
    fun selectDice(die: Dice, diceList: List<Dice>)

    /**
     * Locks a die from the given list of dice.
     */
    fun lockDice(die: Dice, diceList: List<Dice>)

    /**
     * Retrieves the total score of the game.
     */
    fun getTotalScore(): Flow<Int>

    /**
     * Retrieves the calculated score from combinations.
     */
    fun getCalculatedScore(): Flow<Int>

    /**
     * Retrieves the list of score choices.
     */
    fun getScoreChoices(): Flow<List<ScoreChoice>>

    /**
     * Retrieves the history of scores choices.
     */
    fun getScoreHistory(): Flow<List<ScoreHistory>>

    /**
     * Selects a score choice.
     */
    fun selectScoreChoice(selectedScoreChoice: ScoreChoice)

    /**
     * Ends the current turn of the game.
     */
    suspend fun endTurn()

    /**
     * Starts a new game.
     */
    fun startNewGame()

    /**
     * Toggles the state of the help assist.
     */
    fun toggleHelpAssist()

    /**
     * Restores the game state from a previously saved instance.
     *
     * @param gameState The previously saved GameState object that will be used to restore the game.
     * This should include all the necessary information to fully resume the game from the point it was saved.
     */
    fun retrieveSavedGameState(gameState: GameState)


    /**
     * Retrieves the number of throws left.
     */
    val throwsLeftFlow: Flow<Int>

    /**
     * Checks if there are no more throws left.
     */
    val noMoreThrows: Flow<Boolean>

    /**
     * Checks if the game is over.
     */
    val gameOver: Flow<Boolean>

    /**
     * Checks if the help assist is enabled.
     */
    val helpAssistEnabled: Flow<Boolean>

    /**
     * Checks if ending the turn is allowed.
     */
    val endTurnAllowed: Flow<Boolean>

    /**
     * Checks if a throw action has been performed.
     */
    val hasThrown: Flow<Boolean>

    /**
     * Emits a string message every time a turn ends.
     */
    val endTurnEventFlow: Flow<String>

    /**
     * Checks if a score choice has been selected.
     */
    val hasSelectedChoice: Flow<Boolean>

    /**
     * Retrieves the number of rounds left.
     */
    val roundsLeftFlow: Flow<Int>

    /**
     * Checks if a new game has started.
     */
    val newGameFlow: Flow<Boolean>

    /**
     * Checks if the game has been won.
     */
    val hasWon: Flow<Boolean>
}
