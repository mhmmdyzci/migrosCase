package com.example.spaceflightnews.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceflightnews.domain.model.Article
import com.example.spaceflightnews.domain.usecase.GetArticlesUseCase
import com.example.spaceflightnews.domain.usecase.GetCachedArticlesUseCase
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getArticlesUseCase: GetArticlesUseCase,
    private val getCachedArticlesUseCase: GetCachedArticlesUseCase
) : ViewModel() {

    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> = _articles

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    private val _showRetry = MutableLiveData<Boolean>()
    val showRetry: LiveData<Boolean> = _showRetry

    private var currentPage = 0
    private val pageSize = 10
    private var isLoading = false
    private var isLastPage = false

    var isSearching = false

    fun isLoading(): Boolean = isLoading
    fun isLastPage(): Boolean = isLastPage
    fun isCurrentlySearching(): Boolean = isSearching

    fun refreshFromApi(context: Context) {
        currentPage = 0
        _articles.value = emptyList()
        isLastPage = false
        fetchArticles(context)
    }
    
    fun retryFetchArticles(context: Context) {
        _error.value = ""
        _showRetry.value = false
        fetchArticles(context)
    }

    fun fetchArticles(context: Context) {
        viewModelScope.launch {
            _loading.value = true
            isLoading = true
            
            getArticlesUseCase(limit = pageSize, offset = currentPage * pageSize, context)
                .onSuccess { result ->
                    if (result.isEmpty()) {
                        isLastPage = true
                    } else {
                        val currentList = _articles.value ?: emptyList()
                        val newArticles = result.filter { newArticle ->
                            currentList.none { existingArticle -> existingArticle.id == newArticle.id }
                        }
                        _articles.value = currentList + newArticles
                        currentPage++
                    }
                    _showRetry.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.localizedMessage ?: "An error occurred"
                    _showRetry.value = true
                }
            
            _loading.value = false
            isLoading = false
        }
    }

    fun loadCachedArticles() {
        viewModelScope.launch {
            _loading.value = true
            
            getCachedArticlesUseCase()
                .onSuccess { cached ->
                    _articles.value = cached
                }
                .onFailure {
                    _error.value = "Failed to load cached articles."
                }
            
            _loading.value = false
        }
    }
}