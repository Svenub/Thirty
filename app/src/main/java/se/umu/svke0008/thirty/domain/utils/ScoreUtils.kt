package se.umu.svke0008.thirty.domain.utils

import se.umu.svke0008.thirty.domain.model.ScoreChoice

/**
 * A utility object to provide functionalities related to the scoring of the Thirty game.
 * Contains functions to generate score choices and to provide messages based on the player's score.
 */
object ScoreUtils {

    /**
     * Generates a list of possible score choices for the Thirty game.
     *
     * Each score choice represents a different scoring option a player can choose during their turn.
     * The score choices are represented as instances of the [ScoreChoice] class, each with its own
     * unique ID, description, and target score.
     *
     * @return A [List] of [ScoreChoice] representing all possible score choices.
     */
    fun choiceGenerator(): List<ScoreChoice> {
        return listOf(
            ScoreChoice(0, "Value 3 or lower", 3),
            ScoreChoice(1, "Combination of 4", 4),
            ScoreChoice(2, "Combination of 5", 5),
            ScoreChoice(3, "Combination of 6", 6),
            ScoreChoice(4, "Combination of 7", 7),
            ScoreChoice(5, "Combination of 8", 8),
            ScoreChoice(6, "Combination of 9", 9),
            ScoreChoice(7, "Combination of 10", 10),
            ScoreChoice(8, "Combination of 11", 11),
            ScoreChoice(9, "Combination of 12", 12),
        )
    }

    /**
     * Provides a message to the player based on their score.
     *
     * @param score The player's score.
     * @return A [String] containing the message for the player.
     */
    fun getSnackBarMessage(score: Int): String {
        return when (score) {
            in 0..5 -> "Better luck next time!"
            in 6..11 -> "Not bad, keep it up!"
            in 12..17 -> "Good job!"
            in 18..23 -> "Great round!"
            in 24..29 -> "Amazing score!"
            in 30..36 -> "You're on fire!"
            else -> "Invalid score!"
        }
    }
}