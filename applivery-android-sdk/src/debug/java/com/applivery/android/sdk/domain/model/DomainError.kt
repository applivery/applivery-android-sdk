package com.applivery.android.sdk.domain.model

internal abstract class DomainError(val message: String? = null)

internal abstract class DeveloperError : DomainError()

internal class UnauthorizedError : DomainError()
internal class LimitExceededError : DeveloperError()
internal class SubscriptionError : DeveloperError()
internal class InternalError(message: String? = null) : DomainError(message)
