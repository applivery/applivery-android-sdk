package com.applivery.android.sdk.data.api.service

import com.applivery.android.sdk.BuildConfig
import com.applivery.android.sdk.data.api.service.ServiceUriBuilder.withTenant
import org.junit.Assert
import org.junit.Test

class ServiceUriBuilderTests {

    private val customDomain = BuildConfig.CustomDomain
    private val baseUrl = "https://sdk-api"

    @Test
    fun `Given no tenant then URL is built with default domain`() {
        val url = baseUrl.withTenant(tenant = null)
        Assert.assertEquals("$baseUrl.$customDomain/", url)
    }

    @Test
    fun `Given canonical tenant then URL is built with tenant and default domain`() {
        val canonicalTenant = "canonical"
        val url = baseUrl.withTenant(tenant = canonicalTenant)
        Assert.assertEquals("$baseUrl.$canonicalTenant.$customDomain/", url)
    }

    @Test
    fun `Given dedicated tenant then URL is built with dedicated tenant`() {
        val customTenant = "tenant.custom-domain"
        val url = baseUrl.withTenant(tenant = customTenant)
        Assert.assertEquals("$baseUrl.$customTenant/", url)
    }

    @Test
    fun `Given dedicated tenant with subdomains then URL is built with dedicated tenant`() {
        val customTenant = "tenant.subdomain1.subdomain2.custom-domain"
        val url = baseUrl.withTenant(tenant = customTenant)
        Assert.assertEquals("$baseUrl.$customTenant/", url)
    }
}