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
class ArticleMediator(val api: Api, val db: ArticleDatabase) :
    RemoteMediator<Int, HeadlinesResponse.Article>() {

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, HeadlinesResponse.Article>
    ): MediatorResult {

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
            val response = api.searchHeadlines(
                "technology",
                page,
                "in", state.config.pageSize,
                "7be9224ddeaa419e9d9cbffd5de2cf49"
            ).articles
            val isEndOfList = response.isEmpty()
            db.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    db.getRepoDao().clearRemoteKeys()
                    db.articleDao().deleteAllArticles()
                }
                val prevKey = if (page == DEFAULT_PAGE_INDEX) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1
                val keys = response.map {
                    RemoteKeys(repoId = it.title, prevKey = prevKey, nextKey = nextKey)
                }
                db.getRepoDao().insertAll(keys)
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
                val remoteKeys = getClosestRemoteKey(state)
                remoteKeys?.nextKey?.minus(1) ?: DEFAULT_PAGE_INDEX
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                    ?: throw InvalidObjectException("Remote key should not be null for $loadType")
                remoteKeys.nextKey
            }
            LoadType.PREPEND -> {
                val remoteKeys = getFirstRemoteKey(state)
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
    private suspend fun getLastRemoteKey(state: PagingState<Int, HeadlinesResponse.Article>): RemoteKeys? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let { art -> db.getRepoDao().remoteKeysArticleId(art.title) }
    }

    /**
     * get the first remote key inserted which had the data
     */
    private suspend fun getFirstRemoteKey(state: PagingState<Int, HeadlinesResponse.Article>): RemoteKeys? {
        return state.pages
            .firstOrNull() { it.data.isNotEmpty() }
            ?.data?.firstOrNull()
            ?.let { art -> db.getRepoDao().remoteKeysArticleId(art.title) }
    }

    /**
     * get the closest remote key inserted which had the data
     */
    private suspend fun getClosestRemoteKey(state: PagingState<Int, HeadlinesResponse.Article>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.title?.let { repoId ->
                db.getRepoDao().remoteKeysArticleId(repoId)
            }
        }
    }

}