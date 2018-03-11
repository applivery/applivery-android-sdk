package com.applivery.applvsdklib.network.api.responses

class ApiLoginResponse : ServerResponse<ApiAccessToken>()

data class ApiAccessToken(val accessToken: String = "")
