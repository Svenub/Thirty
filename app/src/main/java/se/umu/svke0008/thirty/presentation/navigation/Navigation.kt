package se.umu.svke0008.thirty.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import se.umu.svke0008.thirty.presentation.DiceViewModel
import se.umu.svke0008.thirty.presentation.screen.GameOverScreen
import se.umu.svke0008.thirty.presentation.screen.PlayScreen
import se.umu.svke0008.thirty.presentation.screen.Screen

@Composable
fun Navigation(
) {

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.PlayScreen.route) {


        composable(route = Screen.PlayScreen.route) {
            PlayScreen(
                viewModel = hiltViewModel(),
                navController = navController
            )

        }

        composable(route = Screen.GameOverScreen.route) {
            GameOverScreen(
                viewModel = hiltViewModel(),
                navController = navController
            )
        }
    }
}
