package com.applivery.android.sdk.data.api.service

import arrow.core.Either
import com.applivery.android.sdk.data.models.ApiError
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit

/**
 * A [CallAdapter.Factory] which supports suspend + [Either] as the return type
 *
 * Adding this to [Retrofit] will enable you to return [Either] from your service methods.
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.retrofit.adapter.either.ResponseE
 * import retrofit2.http.GET
 *
 * data class User(val name: String)
 * data class ErrorBody(val msg: String)
 *
 * interface MyService {
 *
 *   @GET("/user/me")
 *   suspend fun getUser(): Either<ErrorBody, User>
 *
 * }
 * ```
 * <!--- KNIT example-arrow-retrofit-01.kt -->
 *
 * Using [Either] as the return type means that 200 status code and HTTP errors
 * return a value, other exceptions will throw.
 *
 * If you want an adapter that never throws but instead wraps all errors (including no network,
 * timeout, malformed JSON) in a dedicated type then define [CallError] as your error type
 * argument:
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.retrofit.adapter.either.networkhandling.CallError
 * import retrofit2.http.Body
 * import retrofit2.http.GET
 * import retrofit2.http.POST
 *
 * data class User(val name: String)
 *
 * interface MyService {
 *
 *   @GET("/user/me")
 *   suspend fun getUser(): Either<CallError, User>
 *
 *   // Set the expected response type as Unit if you expect a null response body
 *   // (e.g. for 204 No Content response)
 *   @POST("/")
 *   suspend fun postSomething(@Body something: String): Either<CallError, Unit>
 * }
 * ```
 * <!--- KNIT example-arrow-retrofit-02.kt -->
 */
internal class EitherCallAdapterFactory : CallAdapter.Factory() {
    companion object {
        fun create(): EitherCallAdapterFactory = EitherCallAdapterFactory()
    }

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        val rawType = getRawType(returnType)

        if (returnType !is ParameterizedType) {
            val name = parseTypeName(returnType)
            throw IllegalArgumentException(
                "Return type must be parameterized as $name<Foo> or $name<out Foo>"
            )
        }

        return when (rawType) {
            Call::class.java -> eitherAdapter(returnType, retrofit)
            else -> null
        }
    }

    private fun eitherAdapter(
        returnType: ParameterizedType,
        retrofit: Retrofit
    ): CallAdapter<Type, out Call<out Any>>? {
        val wrapperType = getParameterUpperBound(0, returnType)
        return when (getRawType(wrapperType)) {
            Either::class.java -> {
                val (errorType, bodyType) = extractErrorAndReturnType(wrapperType, returnType)
                if (errorType == ApiError::class.java) {
                    NetworkEitherCallAdapter(bodyType)
                } else {
                    ArrowEitherCallAdapter<Any, Type>(retrofit, errorType, bodyType)
                }
            }

            else -> null
        }
    }

    private fun extractErrorAndReturnType(
        wrapperType: Type,
        returnType: ParameterizedType
    ): Pair<Type, Type> {
        if (wrapperType !is ParameterizedType) {
            val name = parseTypeName(returnType)
            throw IllegalArgumentException(
                "Return type must be parameterized as " +
                    "$name<ErrorBody, ResponseBody> or $name<out ErrorBody, out ResponseBody>"
            )
        }
        val errorType = getParameterUpperBound(0, wrapperType)
        val bodyType = getParameterUpperBound(1, wrapperType)
        return Pair(errorType, bodyType)
    }
}

private fun parseTypeName(type: Type): String = type.toString().split(".").last()
