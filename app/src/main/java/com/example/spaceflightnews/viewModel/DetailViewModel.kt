package com.example.spaceflightnews.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceflightnews.domain.usecase.UpdateFavoriteUseCase
import com.example.spaceflightnews.domain.usecase.IsArticleFavoritedUseCase
import kotlinx.coroutines.launch

class DetailViewModel(
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
    private val isArticleFavoritedUseCase: IsArticleFavoritedUseCase
) : ViewModel() {

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun isArticleFavorited(articleId: Int) {
        viewModelScope.launch {
            isArticleFavoritedUseCase(articleId)
                .onSuccess { favorited ->
                    _isFavorite.value = favorited
                }
                .onFailure { exception ->
                    _error.value = exception.localizedMessage ?: "Failed to check favorite status"
                }
        }
    }

    fun updateFavorite(articleId: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            updateFavoriteUseCase(articleId, isFavorite)
                .onSuccess {
                    _isFavorite.value = isFavorite
                }
                .onFailure { exception ->
                    _error.value = exception.localizedMessage ?: "Failed to update favorite"
                }
        }
    }
}
