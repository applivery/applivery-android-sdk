package com.applivery.android.sdk.data.api.service

import com.applivery.android.sdk.BuildConfig

internal object ServiceUriBuilder {

    private const val TenantPlaceholder = BuildConfig.TenantPlaceholder

    fun String.buildUponTenant(tenant: String? = null): String {
        return replace(
            oldValue = TenantPlaceholder,
            newValue = tenant?.takeIf { it.isNotBlank() }?.let { "$it." }.orEmpty()
        )
    }
}
