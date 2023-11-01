package se.umu.svke0008.thirty.presentation.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun EndRoundButton(
    text: String,
    onNewRound: () -> Unit,
    isEndTurnEnabled: Boolean
) {
    Button(
        onClick = onNewRound,
        enabled = isEndTurnEnabled
    ) {
        Text(text = text)
    }
}