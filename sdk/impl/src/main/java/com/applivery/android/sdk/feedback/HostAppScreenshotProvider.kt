package com.applivery.android.sdk.feedback

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.applivery.android.sdk.HostActivityProvider
import com.applivery.android.sdk.domain.DomainLogger
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.model.InternalError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

internal interface HostAppScreenshotProvider {

    suspend fun get(): Either<DomainError, Bitmap>
}

@Suppress("DEPRECATION")
internal class HostAppScreenshotProviderImpl(
    private val hostActivityProvider: HostActivityProvider,
    private val logger: DomainLogger
) : HostAppScreenshotProvider {

    override suspend fun get(): Either<DomainError, Bitmap> {
        val activity = hostActivityProvider.activity ?: return InternalError().left()
        return withContext(Dispatchers.IO) {
            try {
                val bitmap = with(activity.window.decorView.getRootView()) {
                    isDrawingCacheEnabled = true
                    val bitmap = Bitmap.createBitmap(drawingCache)
                    isDrawingCacheEnabled = false
                    bitmap
                }
                ByteArrayOutputStream().let {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_BITMAP_QUALITY, it)
                    BitmapFactory.decodeStream(ByteArrayInputStream(it.toByteArray()))
                }.right()
            } catch (e: Throwable) {
                InternalError(e.message).left()
            }
        }.onLeft { logger.errorCapturingScreenFromHostApp(it.message) }
    }

    companion object {
        private const val COMPRESSION_BITMAP_QUALITY: Int = 100
    }
}