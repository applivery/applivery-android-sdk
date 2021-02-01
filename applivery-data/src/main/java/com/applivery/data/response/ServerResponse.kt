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

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import retrofit2.HttpException

class ServerResponse<T>(
    @SerializedName("status")
    val successful: Boolean,
    @SerializedName("data")
    val data: T,
    @SerializedName("error")
    val error: ApiError
) {
    companion object {

        fun parseError(error: HttpException): ApiError? {
            val gson = Gson()
            val type = object : TypeToken<ServerResponse<Any>>() {}.type

            val errorBody = error.response()?.errorBody()?.string()

            val errorResponse: ServerResponse<Any>? = try {
                gson.fromJson(errorBody, type)
            } catch (jsonSyntaxException: JsonSyntaxException) {
                null
            }

            return errorResponse?.error
        }
    }
}
