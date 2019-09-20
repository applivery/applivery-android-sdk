package com.applivery.base.data

class ServerResponse<T>(
    status: Boolean,
    data: T,
    error: ApiError
)