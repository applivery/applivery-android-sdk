package com.applivery.android.sdk.feedback.screenshot

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import java.io.InputStream

internal sealed class HostAppScreenshotFormat<T> {
    object AsBitmap : HostAppScreenshotFormat<Bitmap>()
    object AsUri : HostAppScreenshotFormat<Uri>()
}

internal interface HostAppScreenshotProvider {
    suspend fun <T> get(format: HostAppScreenshotFormat<T>): Either<DomainError, T>
}

@Suppress("DEPRECATION")
internal class HostAppScreenshotProviderImpl(
    private val hostActivityProvider: HostActivityProvider,
    private val screenshotFileExporter: ScreenshotFileExporter,
    private val logger: DomainLogger
) : HostAppScreenshotProvider {

    override suspend fun <T> get(format: HostAppScreenshotFormat<T>): Either<DomainError, T> {
        val activity = hostActivityProvider.activity ?: return InternalError().left()
        return withContext(Dispatchers.IO) {
            try {
                val bitmap = captureBitmapFromRoot(activity)
                val compressedBitMap = bitmap.toInputStream()

                @Suppress("UNCHECKED_CAST")
                when (format) {
                    is HostAppScreenshotFormat.AsBitmap -> {
                       val decoded = BitmapFactory.decodeStream(compressedBitMap)
                       decoded.right() as Either<DomainError, T>
                    }

                    is HostAppScreenshotFormat.AsUri -> {
                        screenshotFileExporter.export(compressedBitMap) as Either<DomainError, T>
                    }
                }
            } catch (e: Throwable) {
                InternalError(e.message).left()
            }
        }.onLeft { logger.errorCapturingScreenFromHostApp(it) }
    }

    private fun captureBitmapFromRoot(activity: Activity): Bitmap {
        return with(activity.window.decorView.rootView) {
            isDrawingCacheEnabled = true
            val bitmap = Bitmap.createBitmap(drawingCache)
            isDrawingCacheEnabled = false
            bitmap
        }
    }

    private fun Bitmap.toInputStream(): InputStream {
        return ByteArrayOutputStream().use { stream ->
            compress(Bitmap.CompressFormat.JPEG, COMPRESSION_BITMAP_QUALITY, stream)
            ByteArrayInputStream(stream.toByteArray())
        }
    }

    companion object {
        private const val COMPRESSION_BITMAP_QUALITY: Int = 100
    }
}
