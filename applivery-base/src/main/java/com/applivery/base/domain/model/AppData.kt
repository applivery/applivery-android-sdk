package com.applivery.base.domain.model

data class AppData(
    val id: String,
    val name: String,
    val description: String,
    val slug: String,
    val appConfig: AppConfig
)
