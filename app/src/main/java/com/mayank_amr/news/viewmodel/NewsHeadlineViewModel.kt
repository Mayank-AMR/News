package com.mayank_amr.news.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mayank_amr.news.data.repository.NewsHeadlineRepository

class NewsHeadlineViewModel(
        private val repository: NewsHeadlineRepository
) : ViewModel() {
    private val TAG = "NewsHeadlineViewModel"

    private val currentCategory = MutableLiveData(DEFAULT_CATEGORY)

    init {

        Log.d(TAG, "Created: ")
    }

    val headlines = currentCategory.switchMap { categoryString ->
        Log.d(TAG, "default query : $categoryString ")
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