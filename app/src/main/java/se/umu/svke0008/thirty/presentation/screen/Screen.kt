package se.umu.svke0008.thirty.presentation.screen

sealed class Screen(val route: String) {
    object PlayScreen : Screen("play_screen")
    object GameOverScreen : Screen("game_over_screen")
}
