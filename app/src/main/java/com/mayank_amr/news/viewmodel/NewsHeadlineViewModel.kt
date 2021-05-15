package com.mayank_amr.news.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mayank_amr.news.data.repository.NewsHeadlineRepository

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


    companion object {
        private const val DEFAULT_CATEGORY = "technology"
    }
}