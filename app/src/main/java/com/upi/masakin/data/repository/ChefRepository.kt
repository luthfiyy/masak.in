package com.upi.masakin.data.repository

import com.upi.masakin.data.dao.ChefDao
import com.upi.masakin.data.entities.Chef
import javax.inject.Inject

interface ChefRepository {
    suspend fun getAllChefs(): List<Chef>
    suspend fun insertChef(chefs: List<Chef>)
}

class ChefRepositoryImpl @Inject constructor(
    private val chefDao: ChefDao
) : ChefRepository {
    override suspend fun getAllChefs(): List<Chef> {
        return chefDao.getAllChefs()
    }

    override suspend fun insertChef(chefs: List<Chef>) {
        chefs.forEach { chefDao.insertChef(it) }
    }
}