package com.mayank_amr.news.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mayank_amr.news.data.db.ArticleDatabase
import com.mayank_amr.news.data.repository.paging.ArticleMediator
import com.mayank_amr.news.data.response.HeadlinesResponse
import com.mayank_amr.news.network.Api
import kotlinx.coroutines.flow.Flow

/**
 * @Project News
 * @Created_by Mayank Kumar on 14-05-2021 08:42 PM
 */


class NewsHeadlineRepository(
    private val api: Api,
    private val db: ArticleDatabase
) {
    private val articleDao = db.articleDao()


    /**
     * page size is the only required param, rest is optional
     */
    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = DEFAULT_PAGE_SIZE, enablePlaceholders = true)
    }

    @ExperimentalPagingApi
    fun letArticleFlowDb(category: String, pagingConfig: PagingConfig = getDefaultPageConfig()): Flow<PagingData<HeadlinesResponse.Article>> {
        if (db == null) throw IllegalStateException("Database is not initialized")

        val pagingSourceFactory = { db.articleDao().getArticlesPage(category) }
        return Pager(
                config = pagingConfig,
                pagingSourceFactory = pagingSourceFactory,
                remoteMediator = ArticleMediator(api, db, category)
        ).flow
    }

    suspend fun updateArticle(article: HeadlinesResponse.Article) {
        articleDao.updateArticle(article)
    }


    companion object {
        const val DEFAULT_PAGE_INDEX = 1
        const val DEFAULT_PAGE_SIZE = 10
    }
}