package com.applivery.data

internal object ApiUriBuilder {

    private const val TenantPlaceholder = BuildConfig.TenantPlaceholder

    fun String.buildUponTenant(tenant: String? = null): String {
        return replace(TenantPlaceholder, tenant?.let { "$it." }.orEmpty())
    }
}