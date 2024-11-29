package com.upi.masakin.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.upi.masakin.data.entities.Chef
import com.upi.masakin.data.entities.RecipeEntity

data class ChefWithRecipes(
    @Embedded val chef: Chef, @Relation(
        parentColumn = "id",
        entityColumn = "chefId"
    ) val recipes: List<RecipeEntity>
)