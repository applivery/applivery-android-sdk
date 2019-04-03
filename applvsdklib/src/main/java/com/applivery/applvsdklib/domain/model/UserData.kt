package com.applivery.applvsdklib.domain.model

data class UserData(
  val email: String = "",
  val firstName: String? = null,
  val lastName: String? = null,
  val tags: Collection<String>? = null,
  val username: String = "",
  val password: String = ""
)