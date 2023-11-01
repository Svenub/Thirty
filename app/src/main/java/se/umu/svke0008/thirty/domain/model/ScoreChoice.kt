package se.umu.svke0008.thirty.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScoreChoice(
    val id: Int,
    val text: String,
    val score: Int,
    val selected: Boolean = false,
    val used: Boolean = false
): Parcelable
