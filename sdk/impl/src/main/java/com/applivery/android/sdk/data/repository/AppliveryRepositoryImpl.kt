package com.applivery.android.sdk.data.repository

import arrow.core.Either
import com.applivery.android.sdk.data.api.ApiDataSource
import com.applivery.android.sdk.domain.model.AppConfig
import com.applivery.android.sdk.domain.model.AuthenticationUri
import com.applivery.android.sdk.domain.model.BindUser
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.model.Feedback
import com.applivery.android.sdk.domain.model.User
import com.applivery.android.sdk.domain.repository.AppliveryRepository

internal class AppliveryRepositoryImpl(
    private val api: ApiDataSource
) : AppliveryRepository {

    override suspend fun getConfig(): Either<DomainError, AppConfig> {
        return api.getAppConfig()
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

    override suspend fun sendFeedback(feedback: Feedback): Either<DomainError, Unit> {
        return api.sendFeedback(feedback)
    }
}
