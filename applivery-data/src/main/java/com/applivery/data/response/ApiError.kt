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

import com.applivery.base.domain.model.Failure
import com.google.gson.annotations.SerializedName

data class ApiError(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: Map<String, Any>?
) {
    fun toFailure(): Failure {
        return when (code) {
            LIMIT_EXCEEDED_ERROR -> Failure.LimitExceededError(message)
            UNAUTHORIZED_ERROR -> Failure.UnauthorizedError(message)
            SUBSCRIPTION_ERROR -> Failure.SubscriptionError(message)
            else -> Failure.InternalError(message)
        }
    }
}

const val LIMIT_EXCEEDED_ERROR = 5003
const val UNAUTHORIZED_ERROR = 4004
const val SUBSCRIPTION_ERROR = 5004
