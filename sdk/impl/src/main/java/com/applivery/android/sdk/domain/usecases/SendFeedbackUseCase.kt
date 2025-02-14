package com.applivery.android.sdk.domain.usecases

import arrow.core.Either
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.model.Feedback
import com.applivery.android.sdk.domain.repository.AppliveryRepository

internal interface SendFeedbackUseCase {

    suspend operator fun invoke(feedback: Feedback): Either<DomainError, Unit>
}

internal class SendFeedback(
    private val repository: AppliveryRepository
) : SendFeedbackUseCase {

    override suspend fun invoke(feedback: Feedback): Either<DomainError, Unit> {
        return repository.sendFeedback(feedback)
    }
}