package com.mayank_amr.news.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mayank_amr.news.data.repository.NewsHeadlineRepository
import com.mayank_amr.news.data.response.HeadlinesResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class NewsHeadlineViewModel(
        private val repository: NewsHeadlineRepository
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    sealed class Event {
        data class ShowErrorMessage(val error: Throwable) : Event()
    }

    private val currentCategory = MutableLiveData(DEFAULT_CATEGORY)

    fun searchArticle(category: String) {
        currentCategory.value = category
    }

    @ExperimentalPagingApi
    val articleResultFromDB = currentCategory.asFlow().flatMapLatest { query ->
        query?.let {
            loadArticleFromDB(query)
        } ?: emptyFlow()
    }.cachedIn(viewModelScope)


    @ExperimentalPagingApi
    fun loadArticleFromDB(category: String): Flow<PagingData<HeadlinesResponse.Article>> {
        return repository.letArticleFlowDb(category).cachedIn(viewModelScope)
    }


    fun onFavouriteClick(article: HeadlinesResponse.Article) {
        val currentlyFavourite = article.isFavourite
        val updatedArticle = article.copy(isFavourite = !currentlyFavourite)
        viewModelScope.launch {
            repository.updateArticle(updatedArticle)
        }
    }


    companion object {
        private const val DEFAULT_CATEGORY = "Technology"
    }
}