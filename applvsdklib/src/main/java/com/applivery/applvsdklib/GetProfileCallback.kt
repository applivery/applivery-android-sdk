package com.applivery.applvsdklib

import com.applivery.base.domain.model.UserProfile

interface GetProfileCallback {

    fun onSuccess(userProfile: UserProfile)

    fun onError(message: String)
}
