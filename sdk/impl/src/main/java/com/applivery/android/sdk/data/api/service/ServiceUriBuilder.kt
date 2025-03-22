package com.applivery.android.sdk.data.api.service

import com.applivery.android.sdk.BuildConfig

internal object ServiceUriBuilder {

    private const val DefaultDomain = BuildConfig.CustomDomain

    /**
     * Appends a tenant domain to the end of a base URL, handling default behavior.
     *
     * If the provided tenant is null or blank, it appends the default domain.
     * Otherwise, if the tenant already contains a dot (.), it's considered a
     * complete domain and is used as-is. If it doesn't contain a dot, the
     * default domain is appended to the tenant.
     *
     * @receiver The base URL to which the tenant domain will be appended.
     * @param tenant The tenant, may be null or blank.
     * @return The final URL with the tenant domain appended.
     */
    fun String.withTenant(tenant: String? = null): String {
        val curatedTenant = tenant?.trim()?.takeIf { it.isNotBlank() }
        val baseDomain = curatedTenant
            ?.let { if (tenant.contains(".")) tenant else "$tenant.$DefaultDomain" }
            ?: DefaultDomain
        return "$this.$baseDomain/"
    }
}
