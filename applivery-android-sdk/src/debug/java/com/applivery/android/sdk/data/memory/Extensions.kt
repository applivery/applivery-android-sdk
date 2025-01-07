package com.applivery.android.sdk.data.memory

import arrow.core.Either
import arrow.core.left
import com.applivery.android.sdk.domain.model.DomainError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.atomic.AtomicBoolean

fun <T : Any> observeCacheFlow(
    memoryGet: () -> Flow<Either<DomainError, T>>,
    memorySet: suspend (T) -> Unit,
    apiGet: suspend () -> Either<DomainError, T>,
    forceUpdate: Boolean = false
) = flow {
    val update = AtomicBoolean(forceUpdate)
    val apiCall: suspend () -> Unit = { apiGet().fold({ emit(it.left()) }, { memorySet(it) }) }
    memoryGet().collect { cache ->
        if (update.getAndSet(false) || cache.isLeft()) apiCall()
        else emit(cache)
    }
}

fun <T : Any> observeAllCacheFlow(
    memoryGet: () -> Flow<Either<DomainError, List<T>>>,
    memorySet: suspend (List<T>) -> Unit,
    apiGet: suspend () -> Either<DomainError, List<T>>,
    forceUpdate: Boolean = false
) = flow {
    val update = AtomicBoolean(forceUpdate)
    val apiCall: suspend () -> Unit = { apiGet().fold({ emit(it.left()) }, { memorySet(it) }) }
    memoryGet().collect { cache ->
        if (update.getAndSet(false) || cache.isLeft()) apiCall()
        else emit(cache)
    }
}
