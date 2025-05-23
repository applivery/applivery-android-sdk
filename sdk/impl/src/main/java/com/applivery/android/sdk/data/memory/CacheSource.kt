package com.applivery.android.sdk.data.memory

internal interface Cache {

    fun clear()
}

internal abstract class CacheSource : Cache {

    private val caches: MutableList<Cache> = mutableListOf()

    protected fun <T : Cache> T.addToSource(): T = apply {
        caches.add(this)
    }

    final override fun clear() = caches.forEach(Cache::clear)
}
