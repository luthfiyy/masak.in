package com.upi.masakin.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val description: String,
    val time: String,
    val serving: String,
    val reviews: String,
    val image: Int,
    val isFavorite: Boolean = false,
    val chefId: Int? = null
)