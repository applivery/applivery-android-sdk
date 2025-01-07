package com.applivery.android.sdk.data.service

import com.applivery.android.sdk.BuildConfig

internal object ServiceUriBuilder {

    private const val TenantPlaceholder = BuildConfig.TenantPlaceholder

    fun String.buildUponTenant(tenant: String? = null): String {
        return replace(TenantPlaceholder, tenant?.let { "$it." }.orEmpty())
    }
}
