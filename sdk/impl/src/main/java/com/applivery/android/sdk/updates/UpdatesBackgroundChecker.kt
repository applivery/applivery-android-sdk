package com.applivery.android.sdk.updates

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.applivery.android.sdk.domain.usecases.CheckUpdatesUseCase
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

internal interface UpdatesBackgroundChecker {

    val isEnabled: Boolean

    fun start()

    fun enable(enable: Boolean)
}

internal class UpdatesBackgroundCheckerImpl(
    private val checkUpdatesUseCase: CheckUpdatesUseCase
) : UpdatesBackgroundChecker, DefaultLifecycleObserver {

    private val coroutineScope = MainScope()

    override var isEnabled: Boolean = false

    override fun start() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun enable(enable: Boolean) {
        isEnabled = enable
    }

    override fun onResume(owner: LifecycleOwner) {
        if (isEnabled) {
            coroutineScope.launch { checkUpdatesUseCase(forceUpdate = false) }
        }
    }
}