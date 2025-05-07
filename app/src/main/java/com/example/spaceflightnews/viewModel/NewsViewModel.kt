package com.example.spaceflightnews.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spaceflightnews.network.repository.NewsRepository
import androidx.lifecycle.viewModelScope
import com.example.spaceflightnews.domain.model.Article
import kotlinx.coroutines.launch

class NewsViewModel(
    private val repository: NewsRepository
) : ViewModel() {

    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> = _articles

    fun fetchArticles(limit: Int, offset: Int) {
        viewModelScope.launch {
            try {
                _articles.value = repository.getArticles(limit, offset)
            } catch (e: Exception) {
                Log.e("NewsViewModel", "Hata: ${e.localizedMessage}")
            }
        }
    }
}