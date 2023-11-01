package se.umu.svke0008.thirty.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import se.umu.svke0008.thirty.domain.model.ScoreChoice

@Composable
fun ScoreChoiceButton(
    scoreChoice: ScoreChoice,
    onChoiceClick: (ScoreChoice) -> Unit,
    isChoiceEnable: State<Boolean>,
    modifier: Modifier = Modifier
) {


    val scoreChoiceState = remember { mutableStateOf(scoreChoice) }
    LaunchedEffect(scoreChoice){
        scoreChoiceState.value = scoreChoice
    }

    val backgroundColor by animateColorAsState(
        when {
            scoreChoiceState.value.selected -> Color.Green
            !isChoiceEnable.value || scoreChoice.used -> Color.Gray.copy(alpha = 0.5f)
            else -> MaterialTheme.colorScheme.primary
        }
    )

    val offsetY by animateDpAsState(
        if (scoreChoice.selected) (-30).dp else 0.dp
    )

    // Calculate text color for contrast
    val textColor = if (backgroundColor.luminance() > 0.5) Color.Black else Color.White


    Box(
        modifier = modifier
            .offset(y = offsetY)
            .clip(CircleShape)
            .background(color = backgroundColor)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        if (!scoreChoiceState.value.used && isChoiceEnable.value) {
                            onChoiceClick(scoreChoice)
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (scoreChoice.score == 3) "Low" else scoreChoice.score.toString(),
            fontSize = 20.sp,
            color = textColor,
            modifier = Modifier
                .clip(CircleShape)
        )
    }
}

