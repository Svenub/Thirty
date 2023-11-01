package se.umu.svke0008.thirty.presentation.game_events

import se.umu.svke0008.thirty.domain.model.Dice
import se.umu.svke0008.thirty.domain.model.ScoreChoice

sealed class GameEvent {
    data class ThrowDice(val dices: List<Dice>) : GameEvent()
    data class SelectDice(val die: Dice, val dice: List<Dice>) : GameEvent()
    data class LockDice(val die: Dice, val dice: List<Dice>) : GameEvent()
    data class SelectScoreChoice(val scoreChoice: ScoreChoice) : GameEvent()
    object EndTurn : GameEvent()
    object ToggleHelpAssist : GameEvent()
    object StartNewGame: GameEvent()
}
