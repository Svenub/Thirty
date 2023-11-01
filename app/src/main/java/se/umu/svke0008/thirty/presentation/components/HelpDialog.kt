package se.umu.svke0008.thirty.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import se.umu.svke0008.thirty.GameSettingsConstants

@Composable
fun HelpDialog(
    helpAssistEnabled: Boolean,
    onShowDialog: (Boolean) -> Unit,
    toggleHelpAssist: () -> Unit
) {
    val scrollState = rememberScrollState()
    Dialog(
        onDismissRequest = { onShowDialog(false) },
        content = {
            Column(Modifier.background(MaterialTheme.colorScheme.background)) {
                Text(
                    text = "Instructions",
                    fontSize = 30.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp),
                )
                Box(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp)
                        .weight(1f)
                ) {
                    Text(text = GameSettingsConstants.GAME_INSTRUCTIONS, fontSize = 20.sp)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(text = "Help assist", fontSize = 20.sp)
                    Checkbox(
                        checked = helpAssistEnabled,
                        onCheckedChange = { toggleHelpAssist() }
                    )
                }
                Row(

                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    TextButton(onClick = { onShowDialog(false) }) {
                        Text("OK")
                    }
                }
            }
        }
    )
}

