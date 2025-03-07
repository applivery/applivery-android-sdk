package com.applivery.android.sdk.domain

internal interface FeedbackProgressProvider {

    val isFeedbackInProgress: Boolean
}

internal interface FeedbackProgressUpdater : FeedbackProgressProvider {

    override var isFeedbackInProgress: Boolean
}

internal class FeedbackProgressProviderImpl : FeedbackProgressProvider, FeedbackProgressUpdater {

    override var isFeedbackInProgress = false
}