package com.applivery.android.sdk.data.service

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.applivery.android.sdk.data.base.JsonMapper
import com.applivery.android.sdk.data.models.ApiError
import com.applivery.android.sdk.data.models.ApiErrorSchema
import com.applivery.android.sdk.data.models.IOError
import com.applivery.android.sdk.data.models.InternalError
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * [CallAdapter.Factory] that returns [EitherCallAdapter] or [EitherCallSuspendAdapter]
 * for calls typed with [Either]
 */
class EitherCallAdapterFactory(private val jsonMapper: JsonMapper) : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {

        /*Fail fast if the type is not a ParameterizedType*/
        if (returnType !is ParameterizedType) return null

        /*Check what is our container type. If its of type Call and parameterized with Either, it
         means it comes from a suspend fun, so the adapter needs to handle it*/
        if (getRawType(returnType) == Call::class.java) {
            val enclosingType = getParameterUpperBound(0, returnType)
            if (getRawType(enclosingType) == Either::class.java) {
                val responseType = getParameterUpperBound(1, enclosingType as ParameterizedType)
                return EitherCallSuspendAdapter<Any>(responseType, jsonMapper)
            }
        }

        /*The return type is directly Either, so handle it normally*/
        if (getRawType(returnType) == Either::class.java) {
            val responseType = getParameterUpperBound(1, returnType)
            return EitherCallAdapter<Any>(responseType, jsonMapper)
        }

        return null
    }
}

/**
 * [CallAdapter] that wraps call responses into [Either].
 */
class EitherCallAdapter<T : Any>(
    private val returnType: Type,
    private val jsonMapper: JsonMapper
) : CallAdapter<T, Either<ApiError, T>> {

    override fun adapt(call: Call<T>): Either<ApiError, T> {
        return runCatching { EitherCallMapper(call, jsonMapper).execute() }.fold(
            onSuccess = { it.body() ?: InternalError().left() },
            onFailure = { IOError().left() }
        )
    }

    override fun responseType(): Type = returnType
}

/**
 * [CallAdapter] that wraps suspend call responses into [Either].
 */
class EitherCallSuspendAdapter<T : Any>(
    private val returnType: Type,
    private val jsonMapper: JsonMapper
) : CallAdapter<T, Call<Either<ApiError, T>>> {

    override fun adapt(call: Call<T>): Call<Either<ApiError, T>> {
        return EitherCallMapper(call, jsonMapper)
    }

    override fun responseType(): Type = returnType
}

/**
 * Simple [Call] that allows mapping from source.
 */
abstract class CallMapper<I, O>(protected val source: Call<I>) : Call<O> {

    final override fun cancel() = source.cancel()
    final override fun request(): Request = source.request()
    final override fun isExecuted() = source.isExecuted
    final override fun isCanceled() = source.isCanceled
}

/**
 * [CallMapper] implementation that transforms source response to [Either].
 * This mapper *NEVER* returns a unsuccessful call nor invokes [Callback.onFailure] as
 * all errors are driven through [arrow.core.Either.Left] instances.
 */
class EitherCallMapper<T : Any>(
    source: Call<T>,
    private val jsonMapper: JsonMapper
) : CallMapper<T, Either<ApiError, T>>(source) {

    private fun <T : Any> Response<T>.handle(): Either<ApiError, T> {
        return if (isSuccessful) {
            body()?.right() ?: InternalError().left()
        } else {
            val errorSchema = jsonMapper.run { errorBody()?.string()?.fromJson<ApiErrorSchema>() }
            errorSchema?.toApiError()?.left() ?: InternalError().left()
        }
    }

    override fun execute(): Response<Either<ApiError, T>> {
        return runCatching { source.execute() }.fold(
            onSuccess = { Response.success(it.handle()) },
            onFailure = { Response.success(IOError().left()) }
        )
    }

    override fun enqueue(callback: Callback<Either<ApiError, T>>) {

        source.enqueue(object : Callback<T> {

            override fun onResponse(call: Call<T>, response: Response<T>) {
                callback.onResponse(
                    this@EitherCallMapper,
                    Response.success(response.handle())
                )
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onResponse(
                    this@EitherCallMapper,
                    Response.success(IOError().left())
                )
            }
        })
    }

    override fun clone(): EitherCallMapper<T> = EitherCallMapper(source.clone(), jsonMapper)

    override fun timeout(): Timeout = source.timeout()
}

