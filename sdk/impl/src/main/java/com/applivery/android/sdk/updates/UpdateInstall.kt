package com.applivery.android.sdk.updates

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal enum class UpdateInstallStep {
    Idle,
    Downloading,
    Installing,
    Done
}

internal interface UpdateInstallProgressObserver {
    val step: Flow<UpdateInstallStep>
}

internal interface UpdateInstallProgressSender : UpdateInstallProgressObserver {
    override val step: MutableStateFlow<UpdateInstallStep>
}

internal class UpdateInstallProgressSenderImpl : UpdateInstallProgressSender {

    override val step = MutableStateFlow(UpdateInstallStep.Idle)
}