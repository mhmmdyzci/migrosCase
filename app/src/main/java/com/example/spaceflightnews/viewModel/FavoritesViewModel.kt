package com.example.spaceflightnews.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceflightnews.domain.model.Article
import com.example.spaceflightnews.domain.usecase.GetFavoriteArticlesUseCase
import com.example.spaceflightnews.domain.usecase.UpdateFavoriteUseCase
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val getFavoriteArticlesUseCase: GetFavoriteArticlesUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase
) : ViewModel() {

    private val _favoriteArticles = MutableLiveData<List<Article>>()
    val favoriteArticles: LiveData<List<Article>> = _favoriteArticles

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getFavoriteArticles() {
        viewModelScope.launch {
            _loading.value = true
            
            getFavoriteArticlesUseCase()
                .onSuccess { favorites ->
                    _favoriteArticles.value = favorites
                }
                .onFailure { exception ->
                    _error.value = exception.localizedMessage ?: "Failed to load favorites"
                }
            
            _loading.value = false
        }
    }
}