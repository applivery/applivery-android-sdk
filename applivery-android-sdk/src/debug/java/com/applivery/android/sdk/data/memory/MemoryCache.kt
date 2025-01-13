package com.applivery.android.sdk.data.memory

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.applivery.android.sdk.domain.model.DomainError
import java.util.concurrent.TimeUnit

/**
 * Class that contains helper methods to deal with memory based cache.
 * [Key] type MUST implement [equals] and [hashCode] in order for cache to work properly
 */
internal class MemoryCache<Key, Value : Any>(private val expiration: Expiration? = null) : Cache {

    private val cache: MutableMap<Key, Value> = mutableMapOf()
    private val cachedAt: MutableMap<Key, Long> = mutableMapOf()

    private val lock = Any()

    operator fun get(key: Key): Either<DomainError, Value> = synchronized(lock) {
        return cache[key]?.let {
            val cachedTime = cachedAt[key] ?: 0
            val expirationTime = expiration?.millis ?: Long.MAX_VALUE
            val isExpired = (now() - cachedTime) > expirationTime
            if (!isExpired) it.right() else CacheExpiredError.left()
        } ?: CacheNotFoundError.left()
    }

    operator fun set(key: Key, value: Value) = synchronized(lock) {
        cachedAt[key] = now()
        cache[key] = value
    }

    fun getAll(): Either<DomainError, List<Value>> = synchronized(lock) {
        val values = cache.keys.map(::get).mapNotNull { it.getOrNull() }
        return if (values.isNotEmpty()) values.right() else EmptyCacheError.left()
    }

    fun remove(key: Key) = synchronized(lock) {
        cachedAt.remove(key)
        cache.remove(key)
    }

    fun keys(): List<Key> {
        return cache.keys.toList()
    }

    override fun clear() = synchronized(lock) {
        cachedAt.clear()
        cache.clear()
    }

    private val Expiration.millis: Long get() = TimeUnit.MILLISECONDS.convert(time, timeUnit)
    private fun now() = System.currentTimeMillis()
}

internal object CacheNotFoundError : DomainError()
internal object CacheExpiredError : DomainError()
internal object EmptyCacheError : DomainError()

internal interface Expiration {
    val time: Long
    val timeUnit: TimeUnit
}

internal class ExpirationTime(
    override val time: Long,
    override val timeUnit: TimeUnit
) : Expiration

internal object Hour : Expiration {
    override val time: Long get() = 1
    override val timeUnit: TimeUnit = TimeUnit.HOURS
}

internal class Hours(override val time: Long) : Expiration {
    override val timeUnit: TimeUnit = TimeUnit.HOURS
}

internal object Day : Expiration {
    override val time: Long get() = 1
    override val timeUnit: TimeUnit = TimeUnit.DAYS
}

internal class Days(override val time: Long) : Expiration {
    override val timeUnit: TimeUnit = TimeUnit.DAYS
}
