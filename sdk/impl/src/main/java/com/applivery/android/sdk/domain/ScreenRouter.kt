package com.applivery.android.sdk.domain

import android.app.Activity
import android.content.Intent
import com.applivery.android.sdk.HostActivityProvider
import com.applivery.android.sdk.feedback.FeedbackActivity
import com.applivery.android.sdk.feedback.FeedbackArguments
import com.applivery.android.sdk.feedback.FeedbackSelectorActivity
import com.applivery.android.sdk.updates.ForceUpdateActivity
import com.applivery.android.sdk.updates.SuggestedUpdateActivity

internal interface ScreenRouter {

    fun toForceUpdateScreen(): Boolean

    fun toSuggestedUpdateScreen(): Boolean

    fun toFeedbackSelectorScreen(): Boolean

    fun toFeedbackScreen(arguments: FeedbackArguments): Boolean
}

internal class AndroidScreenRouter(
    private val hostActivityProvider: HostActivityProvider
) : ScreenRouter {

    override fun toForceUpdateScreen(): Boolean = open {
        ForceUpdateActivity.getIntent(this)
    }

    override fun toSuggestedUpdateScreen(): Boolean = open {
        SuggestedUpdateActivity.getIntent(this)
    }

    override fun toFeedbackSelectorScreen(): Boolean = open {
        FeedbackSelectorActivity.getIntent(this)
    }

    override fun toFeedbackScreen(arguments: FeedbackArguments): Boolean = open {
        FeedbackActivity.getIntent(this, arguments)
    }

    private fun open(builder: Activity.() -> Intent): Boolean {
        val activity = hostActivityProvider.activity ?: return false
        activity.startActivity(activity.builder())
        return true
    }
}
