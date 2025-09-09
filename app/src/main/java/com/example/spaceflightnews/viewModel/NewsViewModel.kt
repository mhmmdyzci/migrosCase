package com.example.spaceflightnews.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceflightnews.domain.model.Article
import com.example.spaceflightnews.domain.usecase.*
import com.example.spaceflightnews.util.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsViewModel(
    private val getArticlesUseCase: GetArticlesUseCase,
    private val getArticleByIdUseCase: GetArticleByIdUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
    private val getFavoriteArticlesUseCase: GetFavoriteArticlesUseCase,
    private val getCachedArticlesUseCase: GetCachedArticlesUseCase,
    private val isArticleFavoritedUseCase: IsArticleFavoritedUseCase
) : ViewModel() {

    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> = _articles

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    private val _showRetry = MutableLiveData<Boolean>()
    val showRetry: LiveData<Boolean> = _showRetry

    val _isFavorite= MutableLiveData<Boolean>()
    var isFavorite: LiveData<Boolean> = _isFavorite

    private val _favoriteArticles = MutableLiveData<List<Article>>()
    val favoriteArticles: LiveData<List<Article>> = _favoriteArticles

    private val _article = MutableStateFlow<Event<Article>?>(null)
    val article: StateFlow<Event<Article>?> = _article


    private var currentPage = 0
    private val pageSize = 10
    private var allArticles = mutableListOf<Article>()


    var isSearching = false
    fun isCurrentlySearching(): Boolean = isSearching

    fun refreshFromApi(context: Context) {
        currentPage = 0
        allArticles.clear()
        _articles.value = emptyList()
        isLastPage = false
        fetchArticles(context)
    }
    
    fun retryFetchArticles(context: Context) {
        _error.value = ""
        _showRetry.value = false
        fetchArticles(context)
    }
    private var isLoading = false
    private var isLastPage = false

    fun isLoading(): Boolean = isLoading
    fun isLastPage(): Boolean = isLastPage

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


    fun fetchArticle(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            isLoading = true
            
            getArticleByIdUseCase(id)
                .onSuccess { result ->
                    _article.value = Event(result)
                }
                .onFailure { exception ->
                    _error.value = exception.localizedMessage ?: "An error occurred"
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
                    allArticles.clear()
                    allArticles.addAll(cached)
                    _articles.value = cached
                }
                .onFailure {
                    _error.value = "Failed to load cached articles."
                }
            
            _loading.value = false
        }
    }


    fun updateFavorite(id: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            updateFavoriteUseCase(id, isFavorite)
                .onFailure { exception ->
                    _error.value = exception.localizedMessage ?: "Failed to update favorite"
                }
        }
    }


    fun isArticleFavorited(articleId: Int) {
        viewModelScope.launch {
            isArticleFavoritedUseCase(articleId)
                .onSuccess { result ->
                    _isFavorite.value = result
                }
                .onFailure { exception ->
                    _error.value = exception.localizedMessage ?: "Failed to check favorite status"
                }
        }
    }

    fun getFavoriteArticles() {
        viewModelScope.launch {
            getFavoriteArticlesUseCase()
                .onSuccess { result ->
                    _favoriteArticles.value = result
                }
                .onFailure { exception ->
                    _error.value = exception.localizedMessage ?: "Failed to load favorite articles"
                }
        }
    }


}


