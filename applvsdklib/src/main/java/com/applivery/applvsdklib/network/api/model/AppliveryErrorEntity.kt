package com.applivery.applvsdklib.network.api.model

import com.applivery.applvsdklib.domain.model.ErrorObject
import com.google.gson.Gson
import retrofit2.Response
import java.io.IOException


data class ErrorDataEntityResponse(
  val error: ErrorEntity,
  val status: Boolean
) {

  companion object {

    fun parseError(response: Response<*>): ErrorEntity {

      val gson = Gson()

      return try {
        val response =
          gson.fromJson(response.errorBody()?.string(), ErrorDataEntityResponse::class.java)
        response?.error ?: ErrorEntity(0, "", null)

      } catch (e: IOException) {
        ErrorEntity(0, "empty", null)
      }
    }
  }
}

data class ErrorEntity(
  val code: Int,
  val message: String,
  val `data`: ErrorDataEntity?
) {

  fun toErrorObject() = ErrorObject(
    true,
    message,
    code ?: 0
  )
}


data class ErrorDataEntity(
  val providers: List<String>
)