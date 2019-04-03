package com.applivery.applvsdklib.network.api.requests

import com.applivery.applvsdklib.domain.model.UserData
import com.google.gson.annotations.SerializedName


data class BindUserRequest(
  @SerializedName("email")
  val email: String,
  @SerializedName("firstName")
  val firstName: String?,
  @SerializedName("lastName")
  val lastName: String?,
  @SerializedName("tags")
  val tags: Collection<String>?
) {

  companion object {
    fun fromUserData(userData: UserData): BindUserRequest {
      return BindUserRequest(
        email = userData.email,
        firstName = userData.firstName,
        lastName = userData.lastName,
        tags = userData.tags
      )
    }
  }
}