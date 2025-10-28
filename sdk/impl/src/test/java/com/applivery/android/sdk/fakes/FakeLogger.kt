package com.applivery.android.sdk.fakes

import com.applivery.android.sdk.domain.Logger

class FakeLogger : Logger {
    override fun log(message: String) = Unit
}
