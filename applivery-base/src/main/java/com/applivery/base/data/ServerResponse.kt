package com.applivery.base.data

class ServerResponse<T>(
    val status: Boolean,
    val data: T,
    val error: ApiError
)