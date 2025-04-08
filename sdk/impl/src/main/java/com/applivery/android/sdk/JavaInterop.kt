@file:JvmName("AppliveryInterop")

package com.applivery.android.sdk

import com.applivery.android.sdk.configuration.Configuration

@JvmOverloads
fun init(
    appToken: String,
    tenant: String? = null,
    configuration: Configuration = Configuration.Empty
) = Applivery.init(appToken, tenant, configuration)

fun getInstance() = Applivery.getInstance()
