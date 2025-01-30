package com.applivery.android.sdk.feedback

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.applivery.android.sdk.domain.DomainLogger
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class ScreenshotObserver(
    private val context: Context,
    private val logger: DomainLogger,
    private val onScreenshotDetected: (Uri) -> Unit
) : ContentObserver(Handler(Looper.getMainLooper())) {

    private val contentResolver = context.contentResolver

    private val coroutineScope = MainScope()
    private var lastScreenshotUriJob: Job? = null
    private val lastScreenshotUri = MutableSharedFlow<Uri>()

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        if (uri == null) return
        if (MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() !in uri.toString()) return
        when (val mediaPermissionGrantStatus = MediaPermissionGrantStatusChecker.of(context)) {
            MediaPermissionGrantStatus.Granted -> Unit
            MediaPermissionGrantStatus.PartiallyGranted,
            MediaPermissionGrantStatus.Denied -> {
                logger.logMediaPermissionGrantStatus(mediaPermissionGrantStatus)
                return
            }
        }
        contentResolver.query(
            uri,
            arrayOf(MediaStore.Images.Media.DISPLAY_NAME),
            null,
            null,
            null
        )?.use {
            coroutineScope.launch { lastScreenshotUri.emit(uri) }
        }
    }

    fun register() {
        contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            this
        )
        lastScreenshotUriJob = lastScreenshotUri
            .debounce(DebounceTimeMillis)
            .onEach(onScreenshotDetected)
            .launchIn(coroutineScope)
    }

    fun unregister() {
        contentResolver.unregisterContentObserver(this)
        lastScreenshotUriJob?.cancel()
    }

    companion object {
        private const val DebounceTimeMillis = 500L
    }
}