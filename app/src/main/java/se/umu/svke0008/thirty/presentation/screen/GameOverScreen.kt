package se.umu.svke0008.thirty.presentation.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import se.umu.svke0008.thirty.presentation.DiceViewModel
import se.umu.svke0008.thirty.presentation.game_events.GameEvent

@Composable
fun GameOverScreen(
    navController: NavController,
    viewModel: DiceViewModel
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val score = viewModel.totalScore.collectAsState().value
    val scoreHistory = viewModel.scoreHistory.collectAsState().value
    val gameOver = viewModel.gameOver.collectAsState()
    val hasWon = viewModel.hasWon.collectAsState().value


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Text(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                text = if (hasWon) "Congratulations!" else "You lost!",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )


            Text(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                text = "Score: $score",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

        }



        LazyColumn(
            modifier = if (isLandscape) Modifier
                .height(130.dp)
                .padding(10.dp)
            else Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(scoreHistory) { item ->
                Text(
                    text = "${item.text}: ${item.score}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Button(
            onClick = {
                viewModel.onEvent(GameEvent.StartNewGame)
                navController.navigate(Screen.PlayScreen.route) {
                    popUpTo(Screen.GameOverScreen.route) { inclusive = true }
                }

            }
        ) {
            Text("New game")
        }

    }

}
