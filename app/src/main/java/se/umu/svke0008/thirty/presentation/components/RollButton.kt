package se.umu.svke0008.thirty.presentation.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun RollButton(
    onRoll: () -> Unit,
    text: String,
    noMoreRolls: Boolean
) {
    Button(
        onClick = onRoll,
        enabled = !noMoreRolls
    ) {
        Text(text = text)
    }
}