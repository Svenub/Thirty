package se.umu.svke0008.thirty.domain.model

import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.Color
import kotlinx.parcelize.Parcelize

@Parcelize
data class Dice(
    val id: Int,
    val amount: Int,
    val locked: Boolean = false,
    val selected: Boolean = false,
    @ColorInt val colorInt: Int? = null
) : Parcelable {
    val color: Color
        get() = colorInt?.let { Color(it) } ?: Color.Transparent
}

fun Dice.saveLockedDice(): Boolean{
    return this.locked
}

fun  Dice.saveLockedAndSelectedDices(): Boolean {
    return this.locked && this.selected
}
