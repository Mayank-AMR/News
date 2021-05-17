package com.mayank_amr.news.data.repository.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.mayank_amr.news.data.db.ArticleDatabase
import com.mayank_amr.news.data.db.paging.RemoteKeys
import com.mayank_amr.news.data.repository.NewsHeadlineRepository.Companion.DEFAULT_PAGE_INDEX
import com.mayank_amr.news.data.response.HeadlinesResponse
import com.mayank_amr.news.network.Api
import retrofit2.HttpException
import java.io.IOException
import java.io.InvalidObjectException

/**
 * @Project News
 * @Created_by Mayank Kumar on 16-05-2021 09:03 PM
 */

/**
 * It is responsible for getting the results from the DB for pagination
 * and whenever needed it gets the fresh data from the network as well
 * and saves it to the local DB
 */

@ExperimentalPagingApi
class ArticleMediator(val api: Api, val db: ArticleDatabase, val category: String) : RemoteMediator<Int, HeadlinesResponse.Article>() {
    private val TAG = "ArticleMediator"


    override suspend fun load(loadType: LoadType, state: PagingState<Int, HeadlinesResponse.Article>): MediatorResult {

        val pageKeyData = getKeyPageData(loadType, state)

        val page = when (pageKeyData) {
            is MediatorResult.Success -> {
                return pageKeyData
            }
            else -> {
                pageKeyData as Int
            }
        }

        try {
            var response = api.searchHeadlines(category, page, COUNTRY, state.config.pageSize, API_KEY).articles
            //var response = apiresponse.articles
            for (element in response) {
                element.category = category
            }
            val isEndOfList = response.isEmpty()

            db.withTransaction {

                // clear all old entries from database of newly fetched category.
                if (loadType == LoadType.REFRESH) {
                    db.getRemoteKeyDao().clearRemoteKeys(category)
                    db.articleDao().deleteAllArticles(category)
                }

                val prevKey = if (page == DEFAULT_PAGE_INDEX) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1
                val keys = response.map {
                    RemoteKeys(repoId = it.title, prevKey = prevKey, nextKey = nextKey, category = it.category)
                }
                // Insert Key and Headlines(Articles) to database.
                db.getRemoteKeyDao().insertAll(keys)
                db.articleDao().insertArticles(response)
            }
            return MediatorResult.Success(endOfPaginationReached = isEndOfList)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    /**
     * this returns the page key or the final end of list success result
     */
    suspend fun getKeyPageData(
        loadType: LoadType,
        state: PagingState<Int, HeadlinesResponse.Article>
    ): Any? {
        return when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getClosestRemoteKey(state, category)
                remoteKeys?.nextKey?.minus(1) ?: DEFAULT_PAGE_INDEX
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state, category)
                        ?: throw InvalidObjectException("Remote key should not be null for $loadType")
                remoteKeys.nextKey
            }
            LoadType.PREPEND -> {
                val remoteKeys = getFirstRemoteKey(state, category)
                        ?: throw InvalidObjectException("Invalid state, key should not be null")
                //end of list condition reached
                remoteKeys.prevKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                remoteKeys.prevKey
            }
        }
    }

    /**
     * get the last remote key inserted which had the data
     */
    private suspend fun getLastRemoteKey(state: PagingState<Int, HeadlinesResponse.Article>, category: String): RemoteKeys? {
        return state.pages
                .lastOrNull { it.data.isNotEmpty() }
                ?.data?.lastOrNull()
                ?.let { art -> db.getRemoteKeyDao().remoteKeysArticleId(art.title, category) }
    }

    /**
     * get the first remote key inserted which had the data
     */
    private suspend fun getFirstRemoteKey(state: PagingState<Int, HeadlinesResponse.Article>, category: String): RemoteKeys? {
        return state.pages
                .firstOrNull() { it.data.isNotEmpty() }
                ?.data?.firstOrNull()
                ?.let { art -> db.getRemoteKeyDao().remoteKeysArticleId(art.title, category) }
    }

    /**
     * get the closest remote key inserted which had the data
     */
    private suspend fun getClosestRemoteKey(state: PagingState<Int, HeadlinesResponse.Article>, category: String): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.title?.let { repoId ->
                db.getRemoteKeyDao().remoteKeysArticleId(repoId, category)
            }
        }
    }

    companion object {
        public const val API_KEY = "25b1aa9a016b4fb49dac78474501bbf5"//"7be9224ddeaa419e9d9cbffd5de2cf49"
        public const val COUNTRY = "in"
    }


}