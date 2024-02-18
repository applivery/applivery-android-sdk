package com.applivery.android.sdk.domain.model

sealed class DomainError : Throwable() {
    class LimitExceeded : DomainError()
    class Unauthorized : DomainError()
    class Subscription : DomainError()
    class Internal : DomainError()
}