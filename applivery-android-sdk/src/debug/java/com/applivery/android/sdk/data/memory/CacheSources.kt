package com.applivery.android.sdk.data.memory

class CacheSources(private val sources: List<CacheSource>) : Cache {

    override fun clear() {
        return sources.forEach { it.clear() }
    }
}
