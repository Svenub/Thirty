package se.umu.svke0008.thirty.presentation.screen

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import se.umu.svke0008.thirty.presentation.DiceViewModel
import se.umu.svke0008.thirty.presentation.components.DicePanel
import se.umu.svke0008.thirty.presentation.components.GameControls
import se.umu.svke0008.thirty.presentation.components.HelpDialog
import se.umu.svke0008.thirty.presentation.components.Score
import se.umu.svke0008.thirty.presentation.components.ScoreChoicePanel
import se.umu.svke0008.thirty.presentation.game_events.GameEvent

@Composable
fun PlayScreen(
    viewModel: DiceViewModel,
    navController: NavController
) {


    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val diceList = viewModel.allDice.collectAsStateWithLifecycle().value
    val gameOver = viewModel.gameOver.collectAsState().value
    val endTurnAllowed = viewModel.endTurnAllowed.collectAsState().value
    val hasThrownOnce = viewModel.hasThrown.collectAsStateWithLifecycle()
    val calculatedScore = viewModel.calculatedScore.collectAsState().value
    val newGame = viewModel.newGame.collectAsState().value
    val scoreChoiceList = viewModel.scoreChoices.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val helpAssistEnabled = viewModel.helpAssist.collectAsState().value
    val screenHeight = configuration.screenHeightDp.dp
    val diceRollTrigger = remember {
        mutableStateOf(0)
    }

    LaunchedEffect(gameOver) {
        if (gameOver) {
            navController.navigate(Screen.GameOverScreen.route) {
                popUpTo(Screen.PlayScreen.route) { inclusive = true }
            }
        }
    }

    val showDialog = remember { mutableStateOf(false) }


    LaunchedEffect(diceList) {
        Log.e("PlayScreen", diceList.toString())
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopAppBarWithInfoButton(showDialog) },
        modifier = Modifier.fillMaxSize()
    ) { _ ->


        if (screenHeight > 600.dp) {
            HandleScoreMessage(viewModel = viewModel)
        }

        if (showDialog.value) {
            HelpDialog(
                helpAssistEnabled = helpAssistEnabled,
                onShowDialog = { showDialog.value = false },
                toggleHelpAssist = { viewModel.onEvent(GameEvent.ToggleHelpAssist) })
        }


        //Main Screen
        Box(modifier = Modifier.fillMaxSize()) {
            if (!newGame) {
                NewGameButton(modifier = Modifier.align(Alignment.Center)) {
                    viewModel.onEvent(
                        GameEvent.StartNewGame
                    )
                }

            } else {
                Column(
                    modifier = if (!isLandscape) Modifier.align(Alignment.BottomCenter) else
                        Modifier.align(Alignment.TopCenter)
                ) {

                    DicePanel(
                        diceList = diceList,
                        hasThrownOnce = hasThrownOnce,
                        onDiceSelect = { viewModel.onEvent(GameEvent.SelectDice(it, diceList)) },
                        onDiceLock = { viewModel.onEvent(GameEvent.LockDice(it, diceList)) },
                        modifier = Modifier.fillMaxWidth(),
                        diceRollTrigger = diceRollTrigger.value
                    )


                    // Score choices, score and controls
                    Column(
                        verticalArrangement = Arrangement.Center,
                    ) {

                        ScoreChoicePanel(
                            scoreChoiceList = scoreChoiceList,
                            onSelectScoreChoice = { viewModel.onEvent(GameEvent.SelectScoreChoice(it)) },
                            hasThrownOnce = hasThrownOnce,
                            isLandScape = isLandscape
                        )
                        if (isLandscape) Spacer(modifier = Modifier.height(10.dp))

                        val score: @Composable () -> Unit = {
                            Score(
                                newGame = true,
                                score = viewModel.totalScore.collectAsState().value,
                                modifier = Modifier.align(CenterHorizontally),
                                textSize = 50,
                                calculatedScore = calculatedScore
                            )
                        }

                        GameControls(
                            isLandscape = isLandscape,
                            onThrow = {
                                viewModel.onEvent(GameEvent.ThrowDice(diceList))
                                diceRollTrigger.value++
                            },
                            onEndTurn = { viewModel.onEvent(GameEvent.EndTurn) },
                            throwButtonText = "Throw (${viewModel.throwsLeft.collectAsState().value} left)",
                            endRoundButtonText = "End Turn (${viewModel.roundsLeft.collectAsState().value} left)",
                            noMoreThrows = viewModel.noMoreThrows.collectAsState().value,
                            isEndTurnEnabled = endTurnAllowed,
                            score = score
                        )

                        if (!isLandscape) Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun HandleScoreMessage(viewModel: DiceViewModel) {
    val scoreMessage = viewModel.eventFlow.collectAsState(initial = "").value
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val showSnackBar = remember { mutableStateOf(false) }

    LaunchedEffect(scoreMessage) {
        if (scoreMessage.isNotBlank() && !isLandscape) {
            showSnackBar.value = true
            delay(2000)
            showSnackBar.value = false
        }
    }

    if (showSnackBar.value) {
        Snackbar() {
            Text(text = scoreMessage)
        }
    }
}


@Composable
fun TopAppBarWithInfoButton(showDialog: MutableState<Boolean>) {
    TopAppBar(
        title = { Text("Thirty") },
        actions = {
            IconButton(onClick = { showDialog.value = true }) {
                Icon(Icons.Filled.Info, contentDescription = "Information")
            }
        }
    )
}

@Composable
fun NewGameButton(modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            "New game",
            color = if (backgroundColor.luminance() > 0.5) Color.Black else Color.White
        )
    }
}

