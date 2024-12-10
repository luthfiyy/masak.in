package com.upi.masakin.data.repository

import android.content.Context
import com.upi.masakin.R
import com.upi.masakin.data.api.Meal
import com.upi.masakin.data.api.MealApiService
import com.upi.masakin.data.database.MasakinDatabase
import com.upi.masakin.data.entities.RecipeEntity
import com.upi.masakin.data.entities.RecipeData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecipeRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val masakinDatabase: MasakinDatabase,
    private val ioDispatcher: CoroutineDispatcher,
    private val mealApiService: MealApiService
) {
    private val recipeDao = masakinDatabase.recipeDao()

    suspend fun populateInitialRecipes() = withContext(ioDispatcher) {
        if (recipeDao.getRecipeCount() == 0) {
            val recipes = getInitialRecipes()
            recipeDao.insertRecipes(recipes)
        }
    }

    private fun getInitialRecipes(): List<RecipeEntity> {
        val dataName = context.resources.getStringArray(R.array.data_title)
        val ingredients = RecipeData.getIngredientsList()
        val steps = RecipeData.getStepsList()
        val description = context.resources.getStringArray(R.array.data_description)
        val time = context.resources.getStringArray(R.array.data_time)
        val serving = context.resources.getStringArray(R.array.data_serving)
        val reviews = context.resources.getStringArray(R.array.data_reviews)
        val dataPhoto = context.resources.getStringArray(R.array.data_image)
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
                image = dataPhoto[i],
                chefId = chefId[i],
                videoId = videoId[i],
                rating = (0..50).random() / 10f
            )
            listRecipe.add(recipe)
        }
        return listRecipe
    }

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
                videoId = recipeEntity.videoId,
                rating = recipeEntity.rating
            )
        }
    }

    suspend fun getPopularRecipes(threshold: Float = 3.5f, query: String): List<RecipeEntity> =
        withContext(ioDispatcher) {
            fetchRecipesFromApi(query).filter { it.rating >= threshold }
        }

    suspend fun fetchRecipesFromApi(query: String): List<RecipeEntity> = withContext(ioDispatcher) {
        val response = mealApiService.searchMeals(query)
        if (response.isSuccessful) {
            response.body()?.meals?.map { meal ->
                RecipeEntity(
                    id = meal.idMeal.toInt(),
                    title = meal.strMeal,
                    ingredients = extractIngredients(meal),
                    steps = extractSteps(meal.strInstructions ?: ""),
                    description = meal.strCategory ?: "",
                    time = estimateCookingTime(meal),
                    serving = estimateServing(meal),
                    reviews = (0..10000).random().toString(),
                    image = meal.strMealThumb ?: "",
                    chefId = 0,
                    videoId = extractYoutubeId(meal.strYoutube) ?: "",
                    rating = (0..50).random() / 10f
                )
            } ?: emptyList()
        } else {
            emptyList()
        }
    }

    // Helper function to extract ingredients from Meal
    private fun extractIngredients(meal: Meal): List<String> {
        val ingredients = mutableListOf<String>()
        meal.apply {
            strIngredient1?.let { ingredients.add(it) }
            strIngredient2?.let { ingredients.add(it) }
            strIngredient3?.let { ingredients.add(it) }
            strIngredient4?.let { ingredients.add(it) }
            strIngredient5?.let { ingredients.add(it) }
            strIngredient6?.let { ingredients.add(it) }
            strIngredient7?.let { ingredients.add(it) }
            strIngredient8?.let { ingredients.add(it) }
            strIngredient9?.let { ingredients.add(it) }
            strIngredient10?.let { ingredients.add(it) }
            strIngredient11?.let { ingredients.add(it) }
            strIngredient12?.let { ingredients.add(it) }
            strIngredient13?.let { ingredients.add(it) }
            strIngredient14?.let { ingredients.add(it) }
            strIngredient15?.let { ingredients.add(it) }
            strIngredient16?.let { ingredients.add(it) }
            strIngredient17?.let { ingredients.add(it) }
            strIngredient18?.let { ingredients.add(it) }
            strIngredient19?.let { ingredients.add(it) }
            strIngredient20?.let { ingredients.add(it) }
        }
        return ingredients
    }

    private fun extractSteps(instructions: String): List<String> {
        return instructions.split("\n").filter { it.isNotBlank() }
    }

    private fun extractYoutubeId(youtubeUrl: String?): String? {
        youtubeUrl ?: return null

        val patterns = listOf(
            "https?://(?:www\\.)?youtube\\.com/watch\\?v=([^&]+)",
            "https?://(?:www\\.)?youtube\\.com/embed/([^?&]+)",
            "https?://(?:www\\.)?youtu\\.be/([^?&]+)"
        )

        for (pattern in patterns) {
            val matcher = pattern.toRegex().find(youtubeUrl)
            if (matcher != null) {
                return matcher.groupValues[1]
            }
        }

        return null
    }

    private fun estimateCookingTime(meal: Meal): String {
        val ingredientCount = countIngredients(meal)
        return when {
            ingredientCount <= 5 -> "15 menit"
            ingredientCount <= 10 -> "30 menit"
            else -> "45 menit"
        }
    }

    private fun countIngredients(meal: Meal): Int {
        return listOfNotNull(
            meal.strIngredient1,
            meal.strIngredient2,
            meal.strIngredient3,
            meal.strIngredient4,
            meal.strIngredient5,
            meal.strIngredient6,
            meal.strIngredient7,
            meal.strIngredient8,
            meal.strIngredient9,
            meal.strIngredient10,
            meal.strIngredient11,
            meal.strIngredient12,
            meal.strIngredient13,
            meal.strIngredient14,
            meal.strIngredient15
        ).filter { it.isNotBlank() }.size
    }

    private fun estimateServing(meal: Meal): String {
        val ingredientCount = countIngredients(meal)
        return when {
            ingredientCount <= 5 -> "1-2 porsi"
            ingredientCount <= 10 -> "3-4 porsi"
            else -> "4-6 porsi"
        }
    }
}
