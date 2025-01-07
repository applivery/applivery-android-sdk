package com.applivery.android.sdk.data.memory

import arrow.core.Either
import com.applivery.android.sdk.domain.model.DomainError
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

/**
 * Class that contains helper methods to deal with live memory based cache backed with [Flow].
 * [Key] type MUST implement [equals] and [hashCode] in order for cache to work properly
 */
class FlowMemoryCache<Key, Value : Any>(expiration: Expiration? = null) : Cache {

    /*Channel that emits on every cache change for [getAll] to works*/
    private val allChangeSignal: MutableSharedFlow<Unit> = createSharedFlow(Unit)

    /*Every different key is backed by a channel that will emit a dummy value whenever
    * the [MemoryCache] within it changes*/
    private val cacheChangeSignals: MutableMap<Key, MutableSharedFlow<Unit>> = mutableMapOf()
    private val cache: MemoryCache<Key, Value> = MemoryCache(expiration)

    operator fun get(key: Key): Flow<Either<DomainError, Value>> {
        return channelForKey(key).map { cache[key] }
    }

    operator fun set(key: Key, value: Value) {
        cache[key] = value
        channelForKey(key).tryEmit(Unit)
        allChangeSignal.tryEmit(Unit)
    }

    fun getAll(): Flow<Either<DomainError, List<Value>>> {
        return allChangeSignal.map { cache.getAll() }
    }

    fun remove(key: Key) {
        cache.remove(key)
        channelForKey(key).tryEmit(Unit)
        allChangeSignal.tryEmit(Unit)
    }

    override fun clear() {
        cache.clear()
        cacheChangeSignals.keys.forEach { key -> channelForKey(key).tryEmit(Unit) }
        allChangeSignal.tryEmit(Unit)
    }

    fun keys(): List<Key> {
        return cache.keys()
    }

    private fun channelForKey(key: Key): MutableSharedFlow<Unit> =
        cacheChangeSignals.getOrPut(key) { createSharedFlow(Unit) }

    private fun <T> createSharedFlow(initialValue: T): MutableSharedFlow<T> = MutableSharedFlow<T>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    ).apply { tryEmit(initialValue) }
}
