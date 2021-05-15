package com.mayank_amr.news.data.repository

import androidx.paging.PagingSource
import com.mayank_amr.news.data.response.HeadlinesResponse
import com.mayank_amr.news.network.Api
import retrofit2.HttpException
import java.io.IOException

/**
 * @Project News
 * @Created_by Mayank Kumar on 14-05-2021 08:45 PM
 */

private const val HEADLINE_STARTING_PAGE_INDEX = 1

class HeadlinePagingSource(
    private val api: Api,
    private val category: String
) : PagingSource<Int, HeadlinesResponse.Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HeadlinesResponse.Article> {
        val position = params.key ?: HEADLINE_STARTING_PAGE_INDEX

        return try {
            val response = api.searchHeadlines(
                category,
                position,
                "in",
                params.loadSize,
                "7be9224ddeaa419e9d9cbffd5de2cf49"
            )
            val headlines = response.articles

            LoadResult.Page(
                data = headlines,
                prevKey = if (position == HEADLINE_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (headlines.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }


    }

}