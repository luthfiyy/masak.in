package com.upi.masakin.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "recipes_table")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String = "",
    val ingredients: List<String> = emptyList(),
    val ingredientImages: List<String> = emptyList(),
    val steps: List<String> = emptyList(),
    val description: String = "",
    val time: String = "",
    val serving: String = "",
    val reviews: String = "",
    val image: String = "",
    val isFavorite: Boolean = false,
    val chefId: Int = 0,
    val videoId: String = "",
    val rating: Float = 0f
) : Parcelable

