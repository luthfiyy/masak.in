package com.upi.masakin.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val title: String,
    val ingredients: String,
    val steps: String,
    val description: String,
    val time: String,
    val serving: String,
    @DrawableRes val image: Int
) : Parcelable

