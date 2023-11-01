package se.umu.svke0008.thirty.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import se.umu.svke0008.thirty.domain.model.Dice
import se.umu.svke0008.thirty.utils.UiUtils
import se.umu.svke0008.thirty.utils.UiUtils.toColor


@Composable
fun Dice(
    dice: Dice,
    modifier: Modifier,
    onDiceLock: (Dice) -> Unit,
    onDiceSelect: (Dice) -> Unit,
    hasThrown: State<Boolean>,
    diceRollTrigger: Int
) {
    val diceValue = rememberSaveable{ mutableStateOf(dice.amount) }

    LaunchedEffect(diceRollTrigger) {
        if(dice.locked || diceRollTrigger == 0) {
            return@LaunchedEffect
        }
        val startTime = System.currentTimeMillis()
        val duration = 700
        while (System.currentTimeMillis() - startTime < duration) {
            diceValue.value = (1..6).random()
            delay(50)
        }
        diceValue.value = dice.amount
    }

    Image(
        painter = painterResource(
            id = UiUtils.getDiceImage(dice.copy(amount = diceValue.value))
        ),
        contentDescription = "Dice",
        modifier = modifier
            .border(
                width = 7.dp,
                color = dice.colorInt?.toColor() ?: Color.Transparent,
                shape = RoundedCornerShape(10)
            )
            .clickable {
                if (hasThrown.value) onDiceLock(dice)
            }
    )
}

