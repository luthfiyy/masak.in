package com.upi.masakin.data.repository

import android.content.Context
import com.upi.masakin.R
import com.upi.masakin.data.database.MasakinDatabase
import com.upi.masakin.data.entities.RecipeEntity
import com.upi.masakin.data.entities.RecipeData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class RecipeRepository(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher
) {
    private val database = MasakinDatabase.getDatabase(context)
    private val recipeDao = database.recipeDao()

    // Initial data population method
    suspend fun populateInitialRecipes() = withContext(ioDispatcher) {
        if (recipeDao.getRecipeCount() == 0) {
            val recipes = getInitialRecipes()
            recipeDao.insertRecipes(recipes)
        }
    }

    // Convert Recipe to RecipeEntity for database storage
    private fun getInitialRecipes(): List<RecipeEntity> {
        val dataName = context.resources.getStringArray(R.array.data_title)
        val ingredients = RecipeData.getIngredientsList()
        val steps = RecipeData.getStepsList()
        val description = context.resources.getStringArray(R.array.data_description)
        val time = context.resources.getStringArray(R.array.data_time)
        val serving = context.resources.getStringArray(R.array.data_serving)
        val reviews = context.resources.getStringArray(R.array.data_reviews)
        val dataPhoto = context.resources.obtainTypedArray(R.array.data_image)
        val chefId = context.resources.getIntArray(R.array.data_chefId)
        val videoId = context.resources.getStringArray(R.array.data_videoId)

        val listRecipe = ArrayList<RecipeEntity>()
        for (i in dataName.indices) {
            val recipe = RecipeEntity(
                title = dataName[i],
                ingredients = ingredients[i],
                steps = steps[i],
                description = description[i],
                time = time[i],
                serving = serving[i],
                reviews = reviews[i],
                image = dataPhoto.getResourceId(i, -1),
                chefId = chefId[i],
                videoId = videoId[i]
            )
            listRecipe.add(recipe)
        }
        dataPhoto.recycle()
        return listRecipe
    }

    // Get all recipes from database
    suspend fun getAllRecipes(): List<RecipeEntity> = withContext(ioDispatcher) {
        recipeDao.getAllRecipesSync().map { recipeEntity ->
            RecipeEntity(
                id = recipeEntity.id,
                title = recipeEntity.title,
                ingredients = recipeEntity.ingredients,
                steps = recipeEntity.steps,
                description = recipeEntity.description,
                time = recipeEntity.time,
                serving = recipeEntity.serving,
                reviews = recipeEntity.reviews,
                image = recipeEntity.image,
                chefId = recipeEntity.chefId,
                videoId = recipeEntity.videoId
            )
        }
    }
}
