package com.upi.masakin.data.repository

import android.content.Context
import com.upi.masakin.data.database.MasakinDatabase
import com.upi.masakin.data.entities.Chef

class ChefRepository(context: Context) {
    private val chefDao = MasakinDatabase.getDatabase(context).chefDao()

    suspend fun getAllChefs(): List<Chef> = chefDao.getAllChefs()
    suspend fun insertChef(chefs: List<Chef>) {
        chefs.forEach { chef ->
            chefDao.insertChef(chef)
        }
    }
}