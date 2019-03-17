package com.applivery.applvsdklib.network.api.model

import com.google.gson.annotations.SerializedName


data class LoginEntity(
  @SerializedName("payload")
  val payload: Payload,
  @SerializedName("provider")
  val provider: String = "traditional"
) {
  companion object {
    fun fromUserData(email: String, password: String) = LoginEntity(
      Payload(email, password)
    )
  }
}

data class Payload(
  @SerializedName("user")
  val email: String,
  @SerializedName("password")
  val password: String
)

data class UserDataEntity(
  @SerializedName("bearer")
  val bearer: String = "",
  @SerializedName("user")
  val user: UserEntity?
)

data class UserEntity(
  @SerializedName("createdAt")
  val createdAt: String?,
  @SerializedName("email")
  val email: String?,
  @SerializedName("firstName")
  val firstName: String?,
  @SerializedName("fullName")
  val fullName: String?,
  @SerializedName("id")
  val id: String?,
  @SerializedName("lastName")
  val lastName: String?,
  @SerializedName("role")
  val role: String?
)