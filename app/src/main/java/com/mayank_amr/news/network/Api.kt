package com.mayank_amr.news.network

import com.mayank_amr.news.data.response.HeadlinesResponse
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * @Project News
 * @Created_by Mayank Kumar on 14-05-2021 02:34 PM
 */
interface Api {

    @GET("/v2/top-headlines")
    suspend fun searchHeadlines(
        @Query("category") category: String,
        @Query("page") page: Int,
        @Query("country") country: String,
        @Query("pageSize") perPage: Int,
        @Query("apiKey") apiKey: String

    ): HeadlinesResponse
}