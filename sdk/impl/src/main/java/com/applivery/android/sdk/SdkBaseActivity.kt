package com.applivery.android.sdk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.applivery.android.sdk.di.AppliveryKoinComponent

internal abstract class SdkBaseActivity : ComponentActivity(), AppliveryKoinComponent {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
    }
}