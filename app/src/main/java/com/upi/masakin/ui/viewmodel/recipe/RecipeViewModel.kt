package com.upi.masakin.ui.viewmodel.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.upi.masakin.data.entities.RecipeEntity
import com.upi.masakin.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {
    private val _recipes = MutableStateFlow<List<RecipeEntity>>(emptyList())
    val recipes: StateFlow<List<RecipeEntity>> = _recipes.asStateFlow()

    private val _popularRecipes = MutableStateFlow<List<RecipeEntity>>(emptyList())
    val popularRecipes: StateFlow<List<RecipeEntity>> = _popularRecipes

    init {
        loadRecipes()
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            repository.populateInitialRecipes()
            _recipes.value = repository.getAllRecipes()
            _popularRecipes.value = repository.getPopularRecipes()
        }
    }

    class RecipeViewModelFactory(private val repository: RecipeRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RecipeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    fun searchRecipes(query: String) = recipes.map { list ->
        if (query.isBlank()) list
        else list.filter { recipe ->
            recipe.title.contains(query, ignoreCase = true)
        }
    }

}