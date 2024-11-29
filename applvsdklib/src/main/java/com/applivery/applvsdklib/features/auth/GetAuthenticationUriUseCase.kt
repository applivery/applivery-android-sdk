package com.applivery.applvsdklib.features.auth

import com.applivery.applvsdklib.presentation.ErrorManager
import com.applivery.base.domain.model.AuthenticationUri
import com.applivery.data.AppliveryApiService
import com.applivery.data.response.ServerResponse
import com.applivery.data.response.toDomain
import retrofit2.HttpException

class GetAuthenticationUriUseCase(
    private val apiService: AppliveryApiService,
    private val errorManager: ErrorManager
) {

    suspend operator fun invoke(): Result<AuthenticationUri> {
        return runCatching { apiService.getAuthenticationUri() }
            .mapCatching { requireNotNull(it.data.toDomain()) }
            .onFailure { if (it is HttpException) handleError(it) }
    }

    private fun handleError(exception: HttpException) {
        val error = ServerResponse.parseError(exception)
        error?.toFailure()?.let { errorManager.showError(it) }
    }

    companion object {
        @Volatile
        private var instance: GetAuthenticationUriUseCase? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: buildGetAuthenticationUriUseCase().also {
                    instance = it
                }
            }

        private fun buildGetAuthenticationUriUseCase() = GetAuthenticationUriUseCase(
            apiService = AppliveryApiService.getInstance(),
            errorManager = ErrorManager()
        )
    }
}