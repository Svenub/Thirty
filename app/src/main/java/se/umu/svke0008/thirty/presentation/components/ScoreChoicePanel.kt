package se.umu.svke0008.thirty.presentation.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import se.umu.svke0008.thirty.domain.model.ScoreChoice


@Composable
fun ScoreChoicePanel(
    scoreChoiceList: State<List<ScoreChoice>>,
    onSelectScoreChoice: (ScoreChoice) -> Unit,
    modifier: Modifier = Modifier,
    hasThrownOnce: State<Boolean>,
    isLandScape: Boolean,
) {
    val buttonBounds = remember { mutableListOf<Rect>() }
    val panelPosition = remember { mutableStateOf(Offset.Zero) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val padding = 10.dp
    val totalPadding = padding * scoreChoiceList.value.size
    val maxButtonSize =
        if (isLandScape) ((screenWidth - totalPadding) / scoreChoiceList.value.size) else 100.dp

    var lastSelectedIndex by remember { mutableStateOf(-1) }

    Box(modifier = modifier
        .fillMaxWidth()
        .onGloballyPositioned { coordinates ->
            panelPosition.value = coordinates.positionInRoot()
        }
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { offset ->
                    buttonBounds.forEachIndexed { index, rect ->
                        if (rect.contains(offset) && hasThrownOnce.value) {
                            if (lastSelectedIndex != index && !scoreChoiceList.value[index].used) {
                                lastSelectedIndex = index
                                onSelectScoreChoice(scoreChoiceList.value[index])
                            }
                        }
                    }
                },
                onDrag = { change, _ ->
                    buttonBounds.forEachIndexed { index, rect ->
                        if (rect.contains(change.position) && !scoreChoiceList.value[index].used
                            && hasThrownOnce.value
                        ) {
                            if (lastSelectedIndex != index) {
                                lastSelectedIndex = index
                                onSelectScoreChoice(scoreChoiceList.value[index])
                            }
                        }
                    }
                }
            )
        }
    ) {
        Column {
            if (!isLandScape) {
                Text(
                    if (hasThrownOnce.value) "Select one combination:" else "",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(10.dp)
                        .align(CenterHorizontally)
                )
            }

            val itemsPerRow = if (isLandScape) scoreChoiceList.value.size else 5
            val rows = scoreChoiceList.value.chunked(itemsPerRow)
            rows.forEach { rowItems ->
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowItems.forEach { scoreChoice ->
                        ScoreChoiceButton(
                            scoreChoice,
                            onChoiceClick = { onSelectScoreChoice(scoreChoice) },
                            isChoiceEnable = hasThrownOnce,
                            modifier = Modifier
                                .size(if (isLandScape) maxButtonSize else 70.dp)
                                .onGloballyPositioned { coordinates ->
                                    val globalPosition = coordinates.positionInRoot()
                                    val newRect = Rect(
                                        left = globalPosition.x - panelPosition.value.x,
                                        top = globalPosition.y - panelPosition.value.y,
                                        right = globalPosition.x + coordinates.size.width - panelPosition.value.x,
                                        bottom = globalPosition.y + coordinates.size.height - panelPosition.value.y
                                    )
                                    val existingRect = buttonBounds.find { rect ->
                                        rect.left == newRect.left &&
                                                rect.top == newRect.top &&
                                                rect.right == newRect.right &&
                                                rect.bottom == newRect.bottom
                                    }
                                    if (existingRect == null) {
                                        buttonBounds.add(newRect)
                                    }
                                }
                        )
                    }
                }
            }
        }
    }
}

