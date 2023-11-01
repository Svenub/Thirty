package se.umu.svke0008.thirty.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun GameControls(
    isLandscape: Boolean,
    onThrow: () -> Unit,
    onEndTurn: () -> Unit,
    modifier: Modifier = Modifier,
    throwButtonText: String,
    endRoundButtonText: String,
    noMoreThrows: Boolean,
    isEndTurnEnabled: Boolean,
    score: @Composable (() -> Unit)
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (isLandscape) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RollButton(
                    onRoll = { onThrow() },
                    text = throwButtonText,
                    noMoreRolls = noMoreThrows
                )

                score()

                EndRoundButton(
                    text = endRoundButtonText,
                    onNewRound = { onEndTurn() },
                    isEndTurnEnabled = isEndTurnEnabled
                )
            }
        } else {
            Row {
                score()
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {


                RollButton(
                    onRoll = { onThrow() },
                    text = throwButtonText,
                    noMoreRolls = noMoreThrows
                )



                EndRoundButton(
                    text = endRoundButtonText,
                    onNewRound = { onEndTurn() },
                    isEndTurnEnabled = isEndTurnEnabled
                )
            }
        }
    }


}


