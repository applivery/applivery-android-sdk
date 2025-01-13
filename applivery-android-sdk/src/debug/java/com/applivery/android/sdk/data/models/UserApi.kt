package com.applivery.android.sdk.data.models

import com.google.gson.annotations.SerializedName

internal data class UserApi(
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("id") val id: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("type") val type: UserTypeApi?
)

internal enum class UserTypeApi {
    @SerializedName("user")
    User,

    @SerializedName("employee")
    Employee
}
