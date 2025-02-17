package com.applivery.android.sdk.domain.repository

import arrow.core.Either
import com.applivery.android.sdk.domain.model.AppConfig
import com.applivery.android.sdk.domain.model.AuthenticationUri
import com.applivery.android.sdk.domain.model.BindUser
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.model.Feedback
import com.applivery.android.sdk.domain.model.User

internal interface AppliveryRepository {

    suspend fun getConfig(): Either<DomainError, AppConfig>

    suspend fun getAuthenticationUri(): Either<DomainError, AuthenticationUri>

    suspend fun bindUser(bindUser: BindUser): Either<DomainError, Unit>

    suspend fun unbindUser(): Either<DomainError, Unit>

    suspend fun getUser(): Either<DomainError, User>

    suspend fun sendFeedback(feedback: Feedback): Either<DomainError, Unit>
}