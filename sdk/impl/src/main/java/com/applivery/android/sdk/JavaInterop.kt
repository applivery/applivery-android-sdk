@file:JvmName("AppliveryInterop")

package com.applivery.android.sdk

import com.applivery.android.sdk.configuration.Configuration

@JvmOverloads
fun start(
    appToken: String,
    tenant: String? = null,
    configuration: Configuration = Configuration.Empty
) = Applivery.start(appToken, tenant, configuration)

@JvmOverloads
fun init(
    appToken: String,
    tenant: String? = null,
    configuration: Configuration = Configuration.Empty
) = start(appToken, tenant, configuration)

fun getInstance() = Applivery.getInstance()
