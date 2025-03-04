package com.applivery.android.sdk.domain

import android.content.Context
import com.applivery.android.sdk.R
import com.applivery.android.sdk.domain.model.DeveloperError
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.model.SubscriptionError

internal class UnifiedErrorHandler(
    private val context: Context,
    private val logger: Logger
) {

    fun handle(error: DomainError) {
        when (error) {
            is DeveloperError -> handleDeveloperError(error)
            else -> logger.log(error.message ?: error::class.java.name)
        }
    }

    private fun handleDeveloperError(error: DeveloperError) {
        val errorMessage = when (error) {
            is SubscriptionError -> context.getString(R.string.applivery_error_subscription)
            else -> error.message
        }
        logger.log(errorMessage ?: error::class.java.name)
    }
}