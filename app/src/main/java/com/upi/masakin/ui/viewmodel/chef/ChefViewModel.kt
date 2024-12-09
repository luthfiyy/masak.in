package com.upi.masakin.ui.viewmodel.chef

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upi.masakin.R
import com.upi.masakin.data.dao.ChefDao
import com.upi.masakin.data.entities.Chef
import com.upi.masakin.data.entities.RecipeEntity
import com.upi.masakin.data.repository.ChefRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChefViewModel @Inject constructor(
    private val chefRepository: ChefRepository,
    private val dispatcher: CoroutineDispatcher,
    private val context: Application,
    private val chefDao: ChefDao
) : ViewModel() {
    // State flows for chefs and recipes
    private val _chefs = MutableStateFlow<List<Chef>>(emptyList())
    val chefs: StateFlow<List<Chef>> = _chefs.asStateFlow()

    private val _recipesByChef = MutableStateFlow<List<RecipeEntity>>(emptyList())
    val recipesByChef: StateFlow<List<RecipeEntity>> = _recipesByChef.asStateFlow()

    // Error handling state flow
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Function to get recipes for a specific chef
    fun getRecipesByChefId(chefId: Int): Flow<List<RecipeEntity>> {
        return flow {
            try {
                val recipes = chefDao.getRecipesByChefId(chefId)
                emit(recipes)
                _recipesByChef.value = recipes
            } catch (e: Exception) {
                Log.e("ChefViewModel", "Error getting recipes for chef $chefId", e)
                _error.value = "Failed to retrieve recipes for chef"
                emit(emptyList())
            }
        }.flowOn(dispatcher)
    }

    // Initialization block
    init {
        fetchChefs()
        checkAndInsertSampleChefs()
    }

    // Fetch all chefs from the repository
    private fun fetchChefs() {
        viewModelScope.launch(dispatcher) {
            try {
                val fetchedChefs = chefRepository.getAllChefs()
                _chefs.value = fetchedChefs
                Log.d("ChefViewModel", "Fetched chefs: ${fetchedChefs.size}")
            } catch (e: Exception) {
                Log.e("ChefViewModel", "Error fetching chefs", e)
                _error.value = "Failed to retrieve chef data"
            }
        }
    }

    // Check and insert sample chefs if no chefs exist
    private fun checkAndInsertSampleChefs() {
        viewModelScope.launch(dispatcher) {
            try {
                val existingChefs = chefRepository.getAllChefs()
                if (existingChefs.isEmpty()) {
                    insertSampleChefs()
                }
            } catch (e: Exception) {
                Log.e("ChefViewModel", "Error checking chefs", e)
                insertSampleChefs()
            }
        }
    }

    // Insert sample chefs into the repository
    private fun insertSampleChefs() {
        viewModelScope.launch(dispatcher) {
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

            // Use a single insert call instead of iterating
            chefRepository.insertChef(sampleChefs)
        }
    }
}