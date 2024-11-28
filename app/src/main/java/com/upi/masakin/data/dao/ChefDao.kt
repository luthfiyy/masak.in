package com.upi.masakin.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.upi.masakin.data.entities.Chef
import com.upi.masakin.data.entities.RecipeEntity

@Dao
interface ChefDao {
    @Transaction

    @Query("SELECT * FROM recipes_table WHERE chefId = :chefId")
    suspend fun getRecipesByChefId(chefId: Int): List<RecipeEntity>

    @Insert
    suspend fun insertChef(chef: Chef)

    @Query("SELECT * FROM chef_table")
    suspend fun getAllChefs(): List<Chef>

    @Query("DELETE FROM chef_table")
    suspend fun deleteAllChefs()
}
