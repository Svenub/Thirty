package se.umu.svke0008.thirty.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import se.umu.svke0008.thirty.R
import se.umu.svke0008.thirty.domain.model.Dice

import java.lang.IllegalArgumentException
import kotlin.random.Random

object UiUtils {

    private var usedColors = mutableListOf<Color>()

    fun getRandomColor(): Color {
        val colors = se.umu.svke0008.thirty.ui.theme.allColors

        if(usedColors.size == colors.size) {
            usedColors.clear()
        }

        var getColor = colors[Random.nextInt(0, colors.size)]
        var index = 0

        while(usedColors.any { it == getColor }) {
            getColor = colors[Random.nextInt(0, colors.size)]
            index++
            if(index == colors.size) {
                break
            }
        }
       usedColors.add(getColor)
        return getColor
    }



    fun getDiceImage(dice: Dice): Int {
        return if (dice.selected) {
            getRedDice(dice.amount)
        } else if (dice.locked) {
            getGreyDice(dice.amount)
        } else {
            getWhiteDice(dice.amount)
        }
    }



     fun getWhiteDice(amount: Int): Int {
        return when (amount) {
            1 -> R.drawable.white1
            2 -> R.drawable.white2
            3 -> R.drawable.white3
            4 -> R.drawable.white4
            5 -> R.drawable.white5
            6 -> R.drawable.white6
            else -> throw IllegalArgumentException("Amount must be between 0 and 7")
        }
    }

    private fun getGreyDice(amount: Int): Int {
        return when (amount) {
            1 -> R.drawable.grey1
            2 -> R.drawable.grey2
            3 -> R.drawable.grey3
            4 -> R.drawable.grey4
            5 -> R.drawable.grey5
            6 -> R.drawable.grey6
            else -> throw IllegalArgumentException("Amount must be between 0 and 7")
        }
    }

    private fun getRedDice(amount: Int): Int {
        return when (amount) {
            1 -> R.drawable.red1
            2 -> R.drawable.red2
            3 -> R.drawable.red3
            4 -> R.drawable.red4
            5 -> R.drawable.red5
            6 -> R.drawable.red6
            else -> throw IllegalArgumentException("Amount must be between 0 and 7")
        }
    }


    fun Color.toInt(): Int = toArgb()
    fun Int.toColor(): Color = Color(this)

}