package com.mayank_amr.news.viewmodel

import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mayank_amr.news.data.repository.NewsHeadlineRepository
import com.mayank_amr.news.data.response.HeadlinesResponse
import kotlinx.coroutines.flow.Flow

class NewsHeadlineViewModel(
        private val repository: NewsHeadlineRepository
) : ViewModel() {
    private val currentCategory = MutableLiveData(DEFAULT_CATEGORY)


    val headlines = currentCategory.switchMap { categoryString ->
        //catch data in viewModelScope to immediately deliver when rotate the device
        repository.getHeadlinesResults(categoryString).cachedIn(viewModelScope)
    }

    fun searchHeadlines(category: String) {
        currentCategory.value = category
    }


    @ExperimentalPagingApi
    fun loadArticleFromDB(): Flow<PagingData<HeadlinesResponse.Article>> {
        return repository.letArticleFlowDb().cachedIn(viewModelScope)
    }



    @ExperimentalPagingApi
    fun fetchArticleFlow(): Flow<PagingData<HeadlinesResponse.Article>> {
        return repository.letArticleFlowFromNetwork("technology").cachedIn(viewModelScope)
    }

    @ExperimentalPagingApi
    fun fetchArticleLiveData(): LiveData<PagingData<HeadlinesResponse.Article>> {
        return repository.letArticleLiveDataFromNetwork("technology").cachedIn(viewModelScope)
    }


    companion object {
        private const val DEFAULT_CATEGORY = "technology"
    }
}