package com.mayank_amr.news.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.mayank_amr.news.network.Api

/**
 * @Project News
 * @Created_by Mayank Kumar on 14-05-2021 08:42 PM
 */
class NewsHeadlineRepository(
        private val api: Api
) {
    fun getHeadlinesResults(category: String) = Pager(
            config = PagingConfig(
                    pageSize = 20,
                    maxSize = 60,
                    enablePlaceholders = false
            ),
            pagingSourceFactory = { HeadlinePagingSource(api, category) }
    ).liveData

}