package com.applivery.updates.data.response

data class ApiBuildTokenResponse(
    val id: Int = 0,
    val title: String = "",
    val completed: Boolean = false
) {

    fun toBuildToken(): String {
        return "TEST_324324"
    }
}