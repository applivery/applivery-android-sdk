package com.applivery.base

import com.applivery.base.domain.model.AppConfig
import com.applivery.base.domain.model.AppData

object AppliveryDataManager {

    var appData: AppData? = null
    lateinit var appToken: String

    fun getAppConfig(): AppConfig? = appData?.appConfig
}