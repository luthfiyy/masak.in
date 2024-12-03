package com.upi.masakin.ui.viewmodel.article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.upi.masakin.data.repository.ArticleRepository
import com.upi.masakin.data.entities.Article
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ArticleViewModel(private val repository: ArticleRepository) : ViewModel() {
    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles.asStateFlow()

    init {
        loadArticles()
    }

    private fun loadArticles() {
        viewModelScope.launch {
            repository.populateInitialArticle()
            _articles.value = repository.getAllArticle()
        }
    }

    class ArticleViewModelFactory(private val repository: ArticleRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ArticleViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    fun searchArticle(query: String) = articles.map { list ->
        if (query.isBlank()) list
        else list.filter { article ->
            article.title.contains(query, ignoreCase = true)
        }
    }

}
