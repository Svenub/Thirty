package se.umu.svke0008.thirty.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScoreHistory(
    val text: String,
    val score: Int
): Parcelable
