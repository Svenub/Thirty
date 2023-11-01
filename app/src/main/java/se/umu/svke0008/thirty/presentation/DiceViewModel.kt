package se.umu.svke0008.thirty.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import se.umu.svke0008.thirty.GameSettingsConstants.DICE_AMOUNT
import se.umu.svke0008.thirty.GameSettingsConstants.ROUNDS_AMOUNT
import se.umu.svke0008.thirty.GameSettingsConstants.THROWS_AMOUNT
import se.umu.svke0008.thirty.domain.model.GameState
import se.umu.svke0008.thirty.domain.service.GameService
import se.umu.svke0008.thirty.domain.utils.DiceUtils
import se.umu.svke0008.thirty.domain.utils.ScoreUtils
import se.umu.svke0008.thirty.presentation.game_events.GameEvent
import se.umu.svke0008.thirty.utils.SaveStateHandleKeys.ALL_DICES_KEY
import se.umu.svke0008.thirty.utils.SaveStateHandleKeys.HELP_ASSIST_KEY
import se.umu.svke0008.thirty.utils.SaveStateHandleKeys.NEW_GAME_KEY
import se.umu.svke0008.thirty.utils.SaveStateHandleKeys.ROUNDS_LEFT_KEY
import se.umu.svke0008.thirty.utils.SaveStateHandleKeys.SCORE_CHOICES_KEY
import se.umu.svke0008.thirty.utils.SaveStateHandleKeys.SCORE_HISTORY_KEY
import se.umu.svke0008.thirty.utils.SaveStateHandleKeys.SCORE_KEY
import se.umu.svke0008.thirty.utils.SaveStateHandleKeys.TURNS_LEFT_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class DiceViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val gameService: GameService
) : ViewModel() {


    val newGame = gameService.newGameFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val allDice = gameService.getDice()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val totalScore = gameService.getTotalScore()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val calculatedScore = gameService.getCalculatedScore()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val helpAssist = gameService.helpAssistEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val gameOver = gameService.gameOver
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val endTurnAllowed = gameService.endTurnAllowed
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val hasThrown = gameService.hasThrown
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val throwsLeft = gameService.throwsLeftFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val eventFlow = gameService.endTurnEventFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    val scoreChoices = gameService.getScoreChoices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val noMoreThrows = gameService.noMoreThrows
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), true)

    val roundsLeft = gameService.roundsLeftFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ROUNDS_AMOUNT)

    val scoreHistory = gameService.getScoreHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val hasWon = gameService.hasWon
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    init {
        saveGameState()
        retrieveSavedGameState()
    }


    fun onEvent(event: GameEvent) {
        when (event) {
            is GameEvent.ThrowDice -> { gameService.throwDice(event.dices) }

            is GameEvent.SelectDice -> { gameService.selectDice(event.die, event.dice) }

            is GameEvent.LockDice -> { gameService.lockDice(event.die, event.dice) }

            is GameEvent.SelectScoreChoice -> { gameService.selectScoreChoice(event.scoreChoice) }

            is GameEvent.EndTurn -> viewModelScope.launch { gameService.endTurn() }

            is GameEvent.ToggleHelpAssist -> gameService.toggleHelpAssist()

            is GameEvent.StartNewGame -> gameService.startNewGame()
        }
    }

    /**
     * Saves the current state of the game by launching a coroutine that collects the game state
     * and assigns it to the corresponding keys in the saved state handle.
     */
    private fun saveGameState() {
        viewModelScope.launch {
            gameService.getGameState().collect { gameState ->
                savedStateHandle[NEW_GAME_KEY] = gameState.newGame
                savedStateHandle[HELP_ASSIST_KEY] = gameState.helpAssist
                savedStateHandle[SCORE_KEY] = gameState.totalScore
                savedStateHandle[ROUNDS_LEFT_KEY] = gameState.roundsLeft
                savedStateHandle[TURNS_LEFT_KEY] = gameState.throwsLeft
                savedStateHandle[SCORE_CHOICES_KEY] = gameState.scoreChoices
                savedStateHandle[SCORE_HISTORY_KEY] = gameState.scoreHistory
                savedStateHandle[ALL_DICES_KEY] = gameState.diceList
            }
        }
    }

    /**
     * Retrieves the saved state of the game. If no saved state exists for a certain key,
     * it assigns a default value. The restored game state is then passed on to the game service.
     */
    private fun retrieveSavedGameState() {
        val restoredGameState = GameState(
            newGame = savedStateHandle[NEW_GAME_KEY] ?: false,
            helpAssist = savedStateHandle[HELP_ASSIST_KEY] ?: false,
            totalScore = savedStateHandle[SCORE_KEY] ?: 0,
            roundsLeft = savedStateHandle[ROUNDS_LEFT_KEY] ?: ROUNDS_AMOUNT,
            throwsLeft = savedStateHandle[TURNS_LEFT_KEY] ?: THROWS_AMOUNT,
            scoreChoices = savedStateHandle[SCORE_CHOICES_KEY] ?: ScoreUtils.choiceGenerator(),
            scoreHistory = savedStateHandle[SCORE_HISTORY_KEY] ?: emptyList(),
            diceList = savedStateHandle[ALL_DICES_KEY] ?: DiceUtils.createDice(DICE_AMOUNT)
        )
        gameService.retrieveSavedGameState(restoredGameState)
    }

}