package com.applivery.android.sdk.data.persistence

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
internal annotation class DataStoreEntity(val name: String)
