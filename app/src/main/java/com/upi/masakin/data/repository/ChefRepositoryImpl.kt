package com.upi.masakin.data.repository

import com.upi.masakin.data.dao.ChefDao
import com.upi.masakin.data.entities.Chef
import com.upi.masakin.data.entities.RecipeEntity
import javax.inject.Inject

class ChefRepositoryImpl @Inject constructor(
    private val chefDao: ChefDao
) : ChefRepository {
    override suspend fun getAllChefs(): List<Chef> {
        return chefDao.getAllChefs()
    }

    override suspend fun insertChef(chefs: List<Chef>) {
        chefs.forEach { chefDao.insertChef(it) }
    }

    override suspend fun getRecipesByChefId(chefId: Int): List<RecipeEntity> {
        return chefDao.getRecipesByChefId(chefId)
    }

}