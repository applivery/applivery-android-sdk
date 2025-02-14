package com.applivery.android.sdk.di

import org.koin.core.Koin
import org.koin.core.component.KoinComponent
import org.koin.dsl.koinApplication

internal object AppliveryDiContext {

    val koinApp = koinApplication()
}

internal interface AppliveryKoinComponent : KoinComponent {

    override fun getKoin(): Koin = AppliveryDiContext.koinApp.koin
}