package com.mayank_amr.news.network

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * @Project News
 * @Created_by Mayank Kumar on 16-05-2021 10:59 AM
 */


/**
 * At the time of calling this function we decide the ResultType and RequestType of responses
 */
//inline fun <ResultType, RequestType> networkBoundResource(
//    crossinline query: () -> Flow<ResultType>,// to load from database
//    crossinline fetch: suspend () -> RequestType, // to fetch from network
//    crossinline saveFetchResult: suspend (RequestType) -> Unit,// take data from network and save in db &
//    crossinline shouldFetch: (ResultType) -> Boolean = { true }
//
//) = flow {
//    val data = query().first() // collecting flow(list of Articles) only one time
//    val flow = if (shouldFetch(data)) {
//        emit(Resource.Loading(data))
//
//        try {
//
//            saveFetchResult(fetch())
//            query().map { Resource.Success(it) } // execution to room
//        } catch (throwable: Throwable) {
//
//            query().map { Resource.Error(throwable, it) }
//        }
//    } else {
//        query().map { Resource.Success(it) }  // return current cached data
//    }
//
//    emitAll(flow)
//}

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    crossinline onFetchSuccess: () -> Unit = { },
    crossinline onFetchFailed: (Throwable) -> Unit = { }
) = channelFlow {
    val data = query().first()

    if (shouldFetch(data)) {
        val loading = launch {
            query().collect { send(Resource.Loading(it)) }
        }

        try {
            saveFetchResult(fetch())
            onFetchSuccess()
            loading.cancel()
            query().collect { send(Resource.Success(it)) }
        } catch (t: Throwable) {
            onFetchFailed(t)
            loading.cancel()
            query().collect { send(Resource.Error(t, it)) }
        }
    } else {
        query().collect { send(Resource.Success(it)) }
    }
}