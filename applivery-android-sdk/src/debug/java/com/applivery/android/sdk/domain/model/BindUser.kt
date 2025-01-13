package com.applivery.android.sdk.domain.model

internal data class BindUser(
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val tags: Collection<String>
)