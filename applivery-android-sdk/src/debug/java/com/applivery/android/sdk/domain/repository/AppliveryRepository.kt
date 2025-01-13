package com.applivery.android.sdk.domain.repository

import arrow.core.Either
import com.applivery.android.sdk.domain.model.AppConfig
import com.applivery.android.sdk.domain.model.AuthenticationUri
import com.applivery.android.sdk.domain.model.BindUser
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.model.User

internal interface AppliveryRepository {

    suspend fun getConfig(forceUpdate: Boolean = false): Either<DomainError, AppConfig>

    suspend fun getAuthenticationUri(): Either<DomainError, AuthenticationUri>

    suspend fun bindUser(bindUser: BindUser): Either<DomainError, Unit>

    suspend fun unbindUser(): Either<DomainError, Unit>

    suspend fun getUser(): Either<DomainError, User>
}