package com.applivery.android.sdk.data.persistence

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.applivery.android.sdk.data.persistence.JsonTransformer.fromJson
import com.applivery.android.sdk.data.persistence.JsonTransformer.toJson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

internal interface DataStoreDataSource<T : Any> {

    fun get(): Flow<Result<T>>

    suspend fun set(value: T)

    suspend fun clear()
}

internal interface DynamicDataStoreDataSource<T : Any, K : Any> {

    fun get(key: K): Flow<Result<T>>

    fun getAll(): Flow<List<T>>

    suspend fun set(key: K, value: T): Result<Unit>

    suspend fun delete(key: K): Result<Unit>

    suspend fun clear()
}

private object JsonTransformer {

    private val gson = GsonBuilder().create()

    fun <T : Any> T.toJson(clazz: Class<T>): String = gson.toJson(this, clazz)

    fun <T : Any> String.fromJson(clazz: Class<T>): T? {
        return runCatching { GsonBuilder().create().fromJson(this, clazz) }.getOrNull()
    }
}

private class DataStoreDelegate<T : Any>(
    private val context: Context,
    private val typeClass: Class<T>
) {

    private val dataStore = PreferenceDataStoreFactory.create {
        val name = typeClass.annotations.filterIsInstance<DataStoreEntity>().first().name
        context.preferencesDataStoreFile(name)
    }

    fun get(key: Preferences.Key<String>): Flow<Result<T>> {
        return dataStore.data
            .map { it[key]?.fromJson(typeClass) }
            .distinctUntilChanged()
            .map { it?.let { Result.success(it) } ?: Result.failure(DataStoreValueNotFound()) }
    }

    fun getAll(): Flow<List<T>> {
        return dataStore.data.map { prefs ->
            prefs.asMap().keys
                .filterIsInstance<Preferences.Key<String>>()
                .mapNotNull { get(it).first().getOrNull() }
        }
    }

    suspend fun set(key: Preferences.Key<String>, value: T) {
        dataStore.edit { it[key] = value.toJson(typeClass) }
    }

    suspend fun delete(key: Preferences.Key<String>) {
        dataStore.edit { it.remove(key) }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}

internal abstract class AbstractDataStoreDataSource<T : Any>(
    context: Context
) : DataStoreDataSource<T> {

    protected abstract val typeClass: Class<T>
    private val key by lazy { stringPreferencesKey("data") }
    private val delegate by lazy { DataStoreDelegate(context, typeClass) }

    override fun get(): Flow<Result<T>> = delegate.get(key)

    override suspend fun set(value: T) = delegate.set(key, value)

    override suspend fun clear() = delegate.clear()
}

internal abstract class AbstractDynamicDataStoreDataSource<T : Any, K : Any>(
    private val context: Context
) : DynamicDataStoreDataSource<T, K> {

    protected abstract val typeClass: Class<T>
    protected abstract val keyClass: Class<K>
    private val delegate by lazy { DataStoreDelegate(context, typeClass) }

    override fun get(key: K): Flow<Result<T>> = key.toPreferencesKey().fold(
        onFailure = { flowOf(Result.failure(MalformedKeyError())) },
        onSuccess = { delegate.get(it) }
    )

    override fun getAll(): Flow<List<T>> = delegate.getAll()

    override suspend fun set(key: K, value: T): Result<Unit> {
        return key.toPreferencesKey().map { delegate.set(it, value) }
    }

    override suspend fun delete(key: K): Result<Unit> {
        return key.toPreferencesKey().map { delegate.delete(it) }
    }

    override suspend fun clear() = delegate.clear()

    private fun K.toPreferencesKey(): Result<Preferences.Key<String>> {
        return runCatching { stringPreferencesKey(toJson(keyClass)) }
    }
}

internal inline fun <reified T : Any> dataStoreDataSource(context: Context): DataStoreDataSource<T> {
    return object : AbstractDataStoreDataSource<T>(context) {
        override val typeClass: Class<T> = T::class.java
    }
}

internal inline fun <reified T : Any, reified K : Any> dynamicDataStoreDataSource(
    context: Context
): AbstractDynamicDataStoreDataSource<T, K> {
    return object : AbstractDynamicDataStoreDataSource<T, K>(context) {
        override val typeClass: Class<T> = T::class.java
        override val keyClass: Class<K> = K::class.java
    }
}

class DataStoreValueNotFound : Throwable()
class MalformedKeyError : Throwable()
