package com.applivery.android.sdk.feedback.video.bubble

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.IntSize
import androidx.core.content.getSystemService
import arrow.atomic.AtomicBoolean

internal class VideoReporterFloatingViewManager(context: Context) {
    private val lifecycleOwner = OverlayViewLifecycleOwner()
    private val factory = FloatingViewFactory(
        context = context,
        lifecycleOwner = lifecycleOwner
    )

    fun show(content: @Composable () -> Unit) {
        factory.create(content)
    }

    fun hide() {
        factory.remove()
    }
}

internal class FloatingViewFactory(
    private val context: Context,
    private val lifecycleOwner: OverlayViewLifecycleOwner
) {
    private val windowManager = requireNotNull(context.getSystemService<WindowManager>())
    private var floatingView: ComposeView? = null
    private var floatingViewLayoutParams = baseLayoutParams().apply {
        flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
    }
    private var isComposeOwnerInit = AtomicBoolean(false)

    fun create(content: @Composable () -> Unit) {
        remove()
        floatingView = ComposeView(context).apply {
            setContent {
                Draggable(
                    windowManager = windowManager,
                    containerView = this,
                    layoutParams = floatingViewLayoutParams,
                    updateSize = { size -> updateSize(this, floatingViewLayoutParams, size) },
                    onDragStart = null,
                    onDrag = null,
                    onDragEnd = null,
                    content = { content() }
                )
            }

            visibility = View.INVISIBLE
            compose(this, floatingViewLayoutParams)
        }
    }

    fun remove() {
        try {
            windowManager.removeView(floatingView)
        } catch (_: IllegalArgumentException) {
            //Ignored
        }
    }

    private fun updateSize(
        composeView: ComposeView,
        layoutParams: WindowManager.LayoutParams,
        size: IntSize
    ) {
        windowManager.updateViewLayout(composeView, layoutParams.apply {
            width = size.width
            height = size.height
        })
        composeView.visibility = View.VISIBLE
    }

    private fun compose(composeView: ComposeView, layoutParams: WindowManager.LayoutParams) {
        composeView.consumeWindowInsets = false
        addToComposeLifecycle(composeView)
        windowManager.addView(composeView, layoutParams)
    }

    private fun addToComposeLifecycle(composable: ComposeView) {
        lifecycleOwner.attachToDecorView(composable)
        if (!isComposeOwnerInit.getAndSet(true)) {
            lifecycleOwner.onCreate()
        }
        lifecycleOwner.onResume()
    }

    private fun baseLayoutParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams().apply {
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.TOP or Gravity.START
            format = PixelFormat.TRANSLUCENT
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }
        }
    }
}
