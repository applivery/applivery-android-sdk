package com.applivery.android.sdk.domain

import com.applivery.android.sdk.HostActivityProvider
import com.applivery.android.sdk.updates.ForceUpdateActivity
import com.applivery.android.sdk.updates.SuggestedUpdateActivity

internal interface ScreenRouter {

    fun navigateToForceUpdateScreen()

    fun navigateToSuggestedUpdateScreen()

}

internal class AndroidScreenRouter(
    private val hostActivityProvider: HostActivityProvider
) : ScreenRouter {

    override fun navigateToForceUpdateScreen() {
        val activity = hostActivityProvider.activity ?: return
        activity.startActivity(ForceUpdateActivity.getIntent(activity))
    }

    override fun navigateToSuggestedUpdateScreen() {
        val activity = hostActivityProvider.activity ?: return
        activity.startActivity(SuggestedUpdateActivity.getIntent(activity))
    }
}