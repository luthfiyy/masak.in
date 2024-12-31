package com.upi.masakin.data.repository

import com.upi.masakin.data.entities.Chef
import com.upi.masakin.data.entities.RecipeEntity

interface ChefRepository {
    suspend fun getAllChefs(): List<Chef>
    suspend fun insertChef(chefs: List<Chef>)
    suspend fun getRecipesByChefId(chefId: Int): List<RecipeEntity>
}
