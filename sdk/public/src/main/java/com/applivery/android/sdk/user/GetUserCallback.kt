package com.applivery.android.sdk.user

import com.applivery.android.sdk.domain.model.User

interface GetUserCallback {

    fun onSuccess(user: User)

    fun onError(message: String)
}
