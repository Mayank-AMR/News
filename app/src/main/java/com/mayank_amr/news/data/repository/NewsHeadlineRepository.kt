package com.mayank_amr.news.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
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
    private val API_KEY = "7be9224ddeaa419e9d9cbffd5de2cf49"
    private val articleDao = db.articleDao()


    fun getHeadlinesResults(category: String) = Pager(
        config = PagingConfig(
            pageSize = 10,
            maxSize = 30,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { HeadlinePagingSource(api, category) }
    ).liveData

    //------------------


//    fun getArticles() = networkBoundResource(
//        query = {
//            articleDao.getAllArticles()
//        },
//        fetch = {
//            //delay(2000)
//            api.searchHeadlines("technology",1,"in",10,API_KEY).articles
//        },
//        shouldFetch = {
//            true
//        },
//        saveFetchResult = { articles ->
//            db.withTransaction {
//                articleDao.deleteAllArticles()
//                articleDao.insertArticles(articles)
//            }
//
//        }
//    )
    //------------------


    /**
     * let's define page size, page size is the only required param, rest is optional
     */
    fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = DEFAULT_PAGE_SIZE, enablePlaceholders = false)
    }

    @ExperimentalPagingApi
    fun letArticleFlowDb(pagingConfig: PagingConfig = getDefaultPageConfig()): Flow<PagingData<HeadlinesResponse.Article>> {
        if (db == null) throw IllegalStateException("Database is not initialized")

        val pagingSourceFactory = { db.articleDao().getArticlesPage() }
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = pagingSourceFactory,
            remoteMediator = ArticleMediator(api, db)
        ).flow
    }

    // for live data fetch
    fun letArticleLiveDataFromNetwork(
        category: String,
        pagingConfig: PagingConfig = getDefaultPageConfig()
    ): LiveData<PagingData<HeadlinesResponse.Article>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { HeadlinePagingSource(api, category) }
        ).liveData
    }

    // for Flow fetch
    fun letArticleFlowFromNetwork(
        category: String,
        pagingConfig: PagingConfig = getDefaultPageConfig()
    ): Flow<PagingData<HeadlinesResponse.Article>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { HeadlinePagingSource(api, category) }
        ).flow
    }


    companion object {
        const val DEFAULT_PAGE_INDEX = 1
        const val DEFAULT_PAGE_SIZE = 10
    }
}