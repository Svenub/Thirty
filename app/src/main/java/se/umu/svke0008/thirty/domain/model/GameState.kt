package se.umu.svke0008.thirty.domain.model

data class GameState(
    val newGame: Boolean,
    val helpAssist: Boolean,
    val totalScore: Int,
    val roundsLeft: Int,
    val throwsLeft: Int,
    val scoreChoices: List<ScoreChoice>,
    val scoreHistory: List<ScoreHistory>,
    val diceList: List<Dice>
)
