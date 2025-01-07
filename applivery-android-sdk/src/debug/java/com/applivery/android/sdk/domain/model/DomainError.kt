package com.applivery.android.sdk.domain.model

abstract class DomainError(val message: String? = null)

abstract class DeveloperError : DomainError()

class UnauthorizedError : DomainError()
class LimitExceededError : DeveloperError()
class SubscriptionError : DeveloperError()
class InternalError(message: String? = null) : DomainError(message)
