package com.upi.masakin.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.upi.masakin.data.entities.Chef
import com.upi.masakin.data.entities.RecipeEntity

data class ChefWithRecipes(
    @Embedded val chef: Chef,
    @Relation(
        parentColumn = "id", // Primary Key di Chef
        entityColumn = "chefId" // Foreign Key di RecipeEntity
    )
    val recipes: List<RecipeEntity> // Daftar Resep yang dibuat Chef
)