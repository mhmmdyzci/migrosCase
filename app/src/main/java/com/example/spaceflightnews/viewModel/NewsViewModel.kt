package com.example.spaceflightnews.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spaceflightnews.network.repository.NewsRepository
import androidx.lifecycle.viewModelScope
import com.example.spaceflightnews.domain.model.Article
import com.example.spaceflightnews.util.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {

    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> = _articles

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

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
            try {
                val result = repository.getArticles(limit = pageSize, offset = currentPage * pageSize, context)
                if (result.isEmpty()) {
                    isLastPage = true
                } else {
                    _articles.value = (_articles.value ?: emptyList()) + result
                    currentPage++
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "An error occurred"
            } finally {
                _loading.value = false
                isLoading = false
            }
        }
    }


    fun fetchArticle( id: Int) {
        viewModelScope.launch {
            _loading.value = true
            isLoading = true
            try {
                val result = repository.getArticleById(id)
                _article.value = Event(result)

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "An error occurred"
            } finally {
                _loading.value = false
                isLoading = false
            }
        }
    }




    fun loadCachedArticles() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val cached = repository.getCachedArticles()
                allArticles.clear()
                allArticles.addAll(cached)
                _articles.value = cached
            } catch (e: Exception) {
                _error.value = "Failed to load cached articles."
            } finally {
                _loading.value = false
            }
        }
    }


    fun updateFavorite(id: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.updateFavoriteStatus(id, isFavorite)
        }
    }


    fun isArticleFavorited(articleId: Int) {
        viewModelScope.launch {
            _isFavorite.value =  repository.isArticleFavorited(articleId)
        }
    }

    fun getFavoriteArticles(){
        viewModelScope.launch {
            _favoriteArticles.value = repository.getFavoriteArticles()
        }
    }


}


