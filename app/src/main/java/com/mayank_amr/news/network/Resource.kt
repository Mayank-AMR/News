package com.mayank_amr.news.network

/**
 * @Project News
 * @Created_by Mayank Kumar on 16-05-2021 10:50 AM
 */
sealed class Resource<T>(
    val data: T? = null,
    val error: Throwable? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(throwable: Throwable, data: T? = null) : Resource<T>(data, throwable)

}