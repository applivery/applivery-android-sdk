/*
 * Copyright (c) 2020 Applivery
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.applivery.data.response

import com.google.gson.annotations.SerializedName

data class ApiLogin(
    @SerializedName("payload")
    val payload: Payload,
    @SerializedName("provider")
    val provider: String = "traditional"
) {
    companion object {
        fun fromUserData(email: String, password: String) = ApiLogin(
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
