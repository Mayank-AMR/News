package com.mayank_amr.news.network

import com.mayank_amr.news.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @Project News
 * @Created_by Mayank Kumar on 14-05-2021 02:41 PM
 */
class RemoteDataSource {
    companion object {
        private const val BASE_URL = "https://newsapi.org/"
    }

    fun <Api> buildApi(
        api: Class<Api>
    ): Api {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(
                        OkHttpClient.Builder().also { client ->
                            if (BuildConfig.DEBUG) {
                                val logging = HttpLoggingInterceptor()
                                logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                                client.addInterceptor(logging) }
                        }.build()
                )
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(api)
    }
}