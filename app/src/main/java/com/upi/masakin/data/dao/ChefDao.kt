package com.upi.masakin.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.upi.masakin.data.entities.Chef

@Dao
interface ChefDao {
    @Insert
    suspend fun insertChef(chef: Chef)

    @Query("SELECT * FROM chef_table")
    suspend fun getAllChefs(): List<Chef>

    @Query("DELETE FROM chef_table")
    suspend fun deleteAllChefs()
}
