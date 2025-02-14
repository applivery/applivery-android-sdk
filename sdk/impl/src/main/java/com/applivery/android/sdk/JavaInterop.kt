@file:JvmName("AppliveryInterop")

package com.applivery.android.sdk

fun init(appToken: String) = Applivery.init(appToken)

fun init(appToken: String, tenant: String) = Applivery.init(appToken, tenant)

fun getInstance() = Applivery.getInstance()
