package com.applivery.android.sdk.data.memory

import arrow.core.Either
import com.applivery.android.sdk.domain.model.AppConfig
import com.applivery.android.sdk.domain.model.DomainError

private typealias AppConfigCache = MemoryCache<Unit, AppConfig>

internal class MemoryDataSource : CacheSource() {

    private val appConfigCache = AppConfigCache(expiration = Hour).addToSource()

    fun getAppConfig(): Either<DomainError, AppConfig> {
        return appConfigCache[Unit]
    }

    fun setAppConfig(appConfig: AppConfig) {
        appConfigCache[Unit] = appConfig
    }
}
