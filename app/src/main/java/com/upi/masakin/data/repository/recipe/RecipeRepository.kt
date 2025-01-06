package com.upi.masakin.data.repository.recipe

import android.content.Context
import com.upi.masakin.data.api.recipe.Meal
import com.upi.masakin.data.api.recipe.MealApiService
import com.upi.masakin.data.entities.RecipeEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecipeRepository @Inject constructor(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
    private val mealApiService: MealApiService
) {
    suspend fun getPopularRecipes(threshold: Float = 3.5f, query: String): List<RecipeEntity> =
        withContext(ioDispatcher) {
            fetchRecipesFromApi(query).filter { it.rating >= threshold }
        }

    suspend fun fetchRecipesFromApi(query: String): List<RecipeEntity> = withContext(ioDispatcher) {
        val response = mealApiService.searchMeals(query)
        if (response.isSuccessful) {
            response.body()?.meals?.mapIndexed { index, meal ->
                RecipeEntity(
                    id = meal.idMeal.toInt(),
                    title = meal.strMeal,
                    ingredients = extractIngredients(meal),
                    ingredientImages = getIngredientImages(meal),
                    steps = extractSteps(meal.strInstructions ?: ""),
                    description = meal.strCategory ?: "",
                    time = estimateCookingTime(meal),
                    serving = estimateServing(meal),
                    reviews = (0..10000).random().toString(),
                    image = meal.strMealThumb ?: "",
                    chefId = when (index % 4) {
                        0 -> 1
                        1 -> 2
                        2 -> 3
                        3 -> 4
                        else -> 1
                    },
                    videoId = extractYoutubeId(meal.strYoutube) ?: "",
                    rating = (0..50).random() / 10f
                )
            } ?: emptyList()
        } else {
            emptyList()
        }
    }

    private fun extractIngredients(meal: Meal): List<String> {
        return meal.getIngredientsWithMeasures().mapNotNull { (ingredient, measure) ->
            if (ingredient.isNotBlank()) {
                if (measure.isNotBlank()) {
                    "$ingredient\n$measure".trim()
                } else {
                    ingredient.trim()
                }
            } else {
                null
            }
        }
    }

    private fun getIngredientImages(meal: Meal): List<String> {
        return meal.getIngredientImages()
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
            ingredientCount <= 5 -> "15m"
            ingredientCount <= 10 -> "30m"
            else -> "45m"
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
