package com.upi.masakin.ui.viewmodel.chef

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upi.masakin.R
import com.upi.masakin.data.dao.ChefDao
import com.upi.masakin.data.entities.Chef
import com.upi.masakin.data.entities.RecipeEntity
import com.upi.masakin.data.repository.chef.ChefRepository
import com.upi.masakin.data.repository.recipe.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChefViewModel @Inject constructor(
    private val chefRepository: ChefRepository,
    private val recipeRepository: RecipeRepository,
    private val dispatcher: CoroutineDispatcher,
    private val context: Application,
    private val chefDao: ChefDao
) : ViewModel() {
    // State flows for chefs and recipes
    private val _chefs = MutableStateFlow<List<Chef>>(emptyList())
    val chefs: StateFlow<List<Chef>> = _chefs.asStateFlow()

    private val _recipesByChef = MutableStateFlow<Map<Int, List<RecipeEntity>>>(emptyMap())

    // UI state handling
    sealed class UiState {
        data object Loading : UiState()
        data class Success(val data: List<RecipeEntity>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)

    init {
        viewModelScope.launch {
            initializeData()
        }
    }

    private suspend fun initializeData() {
        try {
            val chefsInDatabase = chefRepository.getAllChefs()
            if (chefsInDatabase.isEmpty()) {
                insertSampleChefs()
            }
            fetchChefs()
            prefetchAllRecipes()
        } catch (e: Exception) {
            Log.e("ChefViewModel", "Error initializing data", e)
            _uiState.value = UiState.Error("Failed to initialize data")
        }
    }

    fun getRecipesByChefId(chefId: Int): Flow<List<RecipeEntity>> = flow {
        _uiState.value = UiState.Loading
        try {
            // First try to get from cached map
            _recipesByChef.value[chefId]?.let {
                emit(it)
                _uiState.value = UiState.Success(it)
                return@flow
            }

            // If not in cache, fetch from database
            val recipes = chefDao.getRecipesByChefId(chefId)
            if (recipes.isNotEmpty()) {
                emit(recipes)
                // Update cache
                _recipesByChef.value += (chefId to recipes)
                _uiState.value = UiState.Success(recipes)
            } else {
                // If no recipes in database, fetch from repository
                val apiRecipes = recipeRepository.fetchRecipesFromApi("").filter { it.chefId == chefId }
                emit(apiRecipes)
                // Update cache
                _recipesByChef.value += (chefId to apiRecipes)
                _uiState.value = UiState.Success(apiRecipes)
            }
        } catch (e: Exception) {
            Log.e("ChefViewModel", "Error getting recipes for chef $chefId", e)
            _uiState.value = UiState.Error("Failed to retrieve recipes for chef")
            emit(emptyList())
        }
    }.flowOn(dispatcher)

    private fun fetchChefs() {
        viewModelScope.launch(dispatcher) {
            try {
                val fetchedChefs = chefRepository.getAllChefs()
                _chefs.value = fetchedChefs
                Log.d("ChefViewModel", "Fetched chefs: ${fetchedChefs.size}")
            } catch (e: Exception) {
                Log.e("ChefViewModel", "Error fetching chefs", e)
                _uiState.value = UiState.Error("Failed to retrieve chef data")
            }
        }
    }

    private suspend fun prefetchAllRecipes() {
        _chefs.value.forEach { chef ->
            try {
                val recipes = chefDao.getRecipesByChefId(chef.id)
                if (recipes.isNotEmpty()) {
                    _recipesByChef.value += (chef.id to recipes)
                }
            } catch (e: Exception) {
                Log.e("ChefViewModel", "Error prefetching recipes for chef ${chef.id}", e)
            }
        }
    }

    private suspend fun insertSampleChefs() {
        val sampleChefs = listOf(
            Chef(
                name = "Chef A",
                description = context.getString(R.string.contoh_desk_chef),
                image = R.drawable.img_chef1
            ),
            Chef(
                name = "Chef B",
                description = context.getString(R.string.contoh_desk_chef),
                image = R.drawable.img_chef2
            ),
            Chef(
                name = "Chef C",
                description = context.getString(R.string.contoh_desk_chef),
                image = R.drawable.img_chef3
            ),
            Chef(
                name = "Chef D",
                description = context.getString(R.string.contoh_desk_chef),
                image = R.drawable.img_chef4
            )
        )
        chefRepository.insertChef(sampleChefs)
    }

    fun debugChefRecipes() {
        viewModelScope.launch(dispatcher) {
            val allChefs = chefRepository.getAllChefs()
            allChefs.forEach { chef ->
                val recipes = chefDao.getRecipesByChefId(chef.id)
                Log.d("ChefViewModel", "Chef ${chef.name} (ID: ${chef.id}) has ${recipes.size} recipes")
                recipes.forEach { recipe ->
                    Log.d("ChefViewModel", "Recipe: ${recipe.title}, ChefId: ${recipe.chefId}")
                }
            }
        }
    }
}