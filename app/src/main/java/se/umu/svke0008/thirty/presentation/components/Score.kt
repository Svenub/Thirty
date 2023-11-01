package se.umu.svke0008.thirty.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

@Composable
fun Score(
    modifier: Modifier = Modifier,
    score: Int,
    calculatedScore: Int,
    textSize: Int,
    newGame: Boolean
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        if (newGame) {
            Text(
                text = score.toString(),
                fontSize = textSize.sp
            )

            if (calculatedScore != 0) {
                val text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.Green)) {
                        append(" +$calculatedScore")
                    }
                }

                Text(
                    text = text,
                    fontSize = textSize.sp
                )
            }
        }
    }
}

