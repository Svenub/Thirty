package se.umu.svke0008.thirty.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import se.umu.svke0008.thirty.domain.model.Dice

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DicePanel(
    diceList: List<Dice>,
    hasThrownOnce: State<Boolean>,
    onDiceSelect: (Dice) -> Unit,
    onDiceLock: (Dice) -> Unit,
    modifier: Modifier = Modifier,
    diceRollTrigger: Int
) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val padding = 10.dp
    val totalPadding = padding * diceList.size
    val maxDiceSize = if (isLandscape) ((screenWidth - totalPadding) / diceList.size) else 100.dp


    Column(
        modifier = modifier
    ) {
        FlowRow(
            modifier = Modifier.align(CenterHorizontally)
        ) {
            diceList.forEach { dice ->
                Dice(
                    dice = dice,
                    hasThrown = hasThrownOnce,
                    onDiceSelect = onDiceSelect,
                    onDiceLock = onDiceLock,
                    modifier = Modifier
                        .size(maxDiceSize)
                        .padding(5.dp),
                    diceRollTrigger = diceRollTrigger
                )
            }
        }
    }
}

