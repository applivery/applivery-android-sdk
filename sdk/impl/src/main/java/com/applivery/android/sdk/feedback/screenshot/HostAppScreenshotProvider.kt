package com.applivery.android.sdk.feedback.screenshot

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import com.applivery.android.sdk.HostActivityProvider
import com.applivery.android.sdk.domain.DomainLogger
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.model.InternalError
import com.applivery.android.sdk.updates.createContentFile
import com.applivery.android.sdk.updates.getContentUriForFile
import com.applivery.android.sdk.updates.write
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal sealed class HostAppScreenshotFormat<T> {
    object AsBitmap : HostAppScreenshotFormat<Bitmap>()
    object AsUri : HostAppScreenshotFormat<Uri>()
}

internal interface HostAppScreenshotProvider {
    suspend fun <T> get(format: HostAppScreenshotFormat<T>): Either<DomainError, T>
}

internal class HostAppScreenshotProviderImpl(
    private val hostActivityProvider: HostActivityProvider,
    private val logger: DomainLogger
) : HostAppScreenshotProvider {

    override suspend fun <T> get(format: HostAppScreenshotFormat<T>): Either<DomainError, T> {
        val activity = hostActivityProvider.activity ?: return InternalError().left()
        return try {
            val bitmap = withContext(Dispatchers.Main) { captureBitmap(activity) }

            @Suppress("UNCHECKED_CAST")
            when (format) {
                is HostAppScreenshotFormat.AsBitmap -> bitmap.right() as Either<DomainError, T>

                is HostAppScreenshotFormat.AsUri -> withContext(Dispatchers.IO) {
                    bitmap.toInputStream().asUri(context = activity) as Either<DomainError, T>
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            InternalError(e.message).left()
        }.onLeft { logger.errorCapturingScreenFromHostApp(it) }
    }

    private suspend fun captureBitmap(activity: Activity): Bitmap {
        val window = activity.window
        val view = window.decorView
        check(view.width > 0 && view.height > 0) { "Host window has not been laid out yet" }
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // Hardware bitmaps don't exist before API 26, so a software draw is safe here
            view.draw(Canvas(bitmap))
            return bitmap
        }

        return suspendCancellableCoroutine { continuation ->
            PixelCopy.request(
                window,
                bitmap,
                { result ->
                    if (result == PixelCopy.SUCCESS) {
                        continuation.resume(bitmap)
                    } else {
                        continuation.resumeWithException(
                            IllegalStateException("PixelCopy failed with result $result")
                        )
                    }
                },
                Handler(Looper.getMainLooper())
            )
        }
    }

    private fun Bitmap.toInputStream(): InputStream {
        return ByteArrayOutputStream().use { stream ->
            compress(Bitmap.CompressFormat.JPEG, COMPRESSION_BITMAP_QUALITY, stream)
            ByteArrayInputStream(stream.toByteArray())
        }
    }

    private suspend fun InputStream.asUri(context: Context): Either<DomainError, Uri> = either {
        val file = context.createContentFile(generateFileName())
        file.write(this@asUri).bind()
        context.getContentUriForFile(file)
    }

    companion object {
        private const val COMPRESSION_BITMAP_QUALITY: Int = 100
        private const val FEEDBACK_FILE_PREFIX: String = "applivery_feedback"
        private fun generateFileName(): String =
            "${FEEDBACK_FILE_PREFIX}_${System.currentTimeMillis()}.jpeg"
    }
}
