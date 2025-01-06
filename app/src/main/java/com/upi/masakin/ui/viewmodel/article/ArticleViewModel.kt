package com.upi.masakin.ui.viewmodel.article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upi.masakin.data.repository.article.ArticleRepository
import com.upi.masakin.data.entities.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val repository: ArticleRepository
) : ViewModel() {
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

    fun searchArticle(query: String) = articles.map { list ->
        if (query.isBlank()) list
        else list.filter { article ->
            article.title.contains(query, ignoreCase = true)
        }
    }
}