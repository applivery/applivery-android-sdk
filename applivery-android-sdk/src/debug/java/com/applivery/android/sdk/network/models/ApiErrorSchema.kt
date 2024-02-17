package com.applivery.android.sdk.network.models

import com.google.gson.annotations.SerializedName

abstract class ApiError(message: String?) : Throwable(message)

data class ApiErrorSchema(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Map<String, Any>?
) {
    fun toApiError(): ApiError {
        return when (code) {
            LIMIT_EXCEEDED_ERROR -> LimitExceededError(message)
            UNAUTHORIZED_ERROR -> UnauthorizedError(message)
            SUBSCRIPTION_ERROR -> SubscriptionError(message)
            else -> InternalError(message)
        }
    }
}

private const val LIMIT_EXCEEDED_ERROR = 5003
private const val UNAUTHORIZED_ERROR = 4004
private const val SUBSCRIPTION_ERROR = 5004

class LimitExceededError(message: String?) : ApiError(message)
class UnauthorizedError(message: String?) : ApiError(message)
class SubscriptionError(message: String?) : ApiError(message)
class InternalError(message: String? = null) : ApiError(message)
class IOError : ApiError(null)
