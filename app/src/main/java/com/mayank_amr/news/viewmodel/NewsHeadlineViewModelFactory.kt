package com.mayank_amr.news.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mayank_amr.news.data.repository.NewsHeadlineRepository

/**
 * @Project News
 * @Created_by Mayank Kumar on 14-05-2021 10:49 PM
 */
@Suppress("UNCHECKED_CAST")
class NewsHeadlineViewModelFactory(
    private val repository: NewsHeadlineRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(NewsHeadlineViewModel::class.java) -> NewsHeadlineViewModel(repository) as T
            else -> throw IllegalArgumentException("ViewModelClass Not Found")
        }
    }
}