package com.applivery.android.sdk.updates

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.applivery.android.sdk.domain.usecases.DownloadLastBuildUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

internal interface UpdatesBackgroundDownloader {

    val isEnabled: Boolean

    fun start()

    fun enable(callback: DownloadLastUpdateCallback)

    fun disable()
}

internal class UpdatesBackgroundDownloaderImpl(
    private val context: Context,
    private val downloadLastBuild: DownloadLastBuildUseCase
) : UpdatesBackgroundDownloader, DefaultLifecycleObserver {

    private val coroutineScope = MainScope()

    private var callback: DownloadLastUpdateCallback? = null

    private var downloadJob: Job? = null

    override val isEnabled: Boolean get() = callback != null

    override fun start() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun enable(callback: DownloadLastUpdateCallback) {
        this.callback = callback
    }

    override fun disable() {
        this.callback = null
        this.downloadJob?.cancel()
    }

    override fun onResume(owner: LifecycleOwner) {
        if (isEnabled) {
            downloadJob = coroutineScope.launch {
                downloadLastBuild().asCachedAppUpdateResult(context).fold(
                    onSuccess = { callback?.onSuccess(it) },
                    onFailure = { callback?.onError(it) }
                )
            }
        }
    }
}
