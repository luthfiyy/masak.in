package com.upi.masakin.ui.viewmodel.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upi.masakin.data.entities.RecipeEntity
import com.upi.masakin.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _recipes = MutableStateFlow<List<RecipeEntity>>(emptyList())
    val recipes: StateFlow<List<RecipeEntity>> = _recipes.asStateFlow()

    private val _popularRecipes = MutableStateFlow<List<RecipeEntity>>(emptyList())
    val popularRecipes: StateFlow<List<RecipeEntity>> = _popularRecipes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        searchRecipes("")
    }

    fun searchRecipes(query: String) = viewModelScope.launch {
        _isLoading.value = true
        try {
            val searchResults = repository.fetchRecipesFromApi(query.ifBlank { "a" })
            _recipes.value = searchResults
            _isLoading.value = false
        } catch (e: Exception) {
            _isLoading.value = false
        }
    }

    fun fetchPopularRecipes(query: String, threshold: Float = 3.5f) {
        viewModelScope.launch {
            val recipes = repository.getPopularRecipes(threshold, query)
            _popularRecipes.value = recipes
        }
    }
}
