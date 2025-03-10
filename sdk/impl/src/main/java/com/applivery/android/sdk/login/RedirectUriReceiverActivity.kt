package com.applivery.android.sdk.login

import android.app.Activity
import android.os.Bundle

class RedirectUriReceiverActivity : Activity() {

    public override fun onCreate(savedInstanceBundle: Bundle?) {
        super.onCreate(savedInstanceBundle)

        startActivity(LoginActivity.createResponseHandlingIntent(this, intent.data))
        finish()
    }
}
