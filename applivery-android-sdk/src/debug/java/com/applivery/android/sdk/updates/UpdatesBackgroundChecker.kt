package com.applivery.android.sdk.updates

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.applivery.android.sdk.domain.usecases.CheckUpdatesUseCase
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

internal interface UpdatesBackgroundChecker {

    fun start()

    fun enableCheckForUpdatesBackground(enable: Boolean)
}

internal class UpdatesBackgroundCheckerImpl(
    private val checkUpdatesUseCase: CheckUpdatesUseCase
) : UpdatesBackgroundChecker, DefaultLifecycleObserver {

    private val coroutineScope = MainScope()

    private var checkForUpdatesBackgroundEnabled: Boolean = false

    override fun start() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun enableCheckForUpdatesBackground(enable: Boolean) {
        checkForUpdatesBackgroundEnabled = enable
    }

    override fun onResume(owner: LifecycleOwner) {
        if (checkForUpdatesBackgroundEnabled) {
            coroutineScope.launch { checkUpdatesUseCase() }
        }
    }
}