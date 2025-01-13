package com.applivery.android.sdk.data.repository

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.recover
import com.applivery.android.sdk.data.api.ApiDataSource
import com.applivery.android.sdk.data.memory.MemoryDataSource
import com.applivery.android.sdk.domain.model.AppConfig
import com.applivery.android.sdk.domain.model.AuthenticationUri
import com.applivery.android.sdk.domain.model.BindUser
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.model.User
import com.applivery.android.sdk.domain.repository.AppliveryRepository

internal class AppliveryRepositoryImpl(
    private val api: ApiDataSource,
    private val cache: MemoryDataSource,
) : AppliveryRepository {

    override suspend fun getConfig(forceUpdate: Boolean): Either<DomainError, AppConfig> = either {
        recover(
            block = { if (forceUpdate) raise(ForceUpdate) else cache.getAppConfig().bind() },
            recover = { api.getAppConfig().onRight(cache::setAppConfig).bind() }
        )
    }

    override suspend fun getAuthenticationUri(): Either<DomainError, AuthenticationUri> {
        return api.getAuthenticationUri()
    }

    override suspend fun bindUser(bindUser: BindUser): Either<DomainError, Unit> {
        return api.bindUser(bindUser)
    }

    override suspend fun unbindUser(): Either<DomainError, Unit> {
        return api.unbindUser()
    }

    override suspend fun getUser(): Either<DomainError, User> {
        return api.getUser()
    }
}

private object ForceUpdate : DomainError()