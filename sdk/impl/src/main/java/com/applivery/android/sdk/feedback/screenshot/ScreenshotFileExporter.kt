package com.applivery.android.sdk.feedback.screenshot

import android.content.Context
import android.net.Uri
import arrow.core.Either
import arrow.core.raise.either
import com.applivery.android.sdk.domain.DomainLogger
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.updates.createContentFile
import com.applivery.android.sdk.updates.getContentUriForFile
import com.applivery.android.sdk.updates.write
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

internal interface ScreenshotFileExporter {
    suspend fun export(compressedBitmap: InputStream): Either<DomainError, Uri>
}

internal class ScreenshotFileExporterImpl(
    private val logger: DomainLogger,
    private val context: Context
) : ScreenshotFileExporter {

    override suspend fun export(compressedBitmap: InputStream): Either<DomainError, Uri> {
        return withContext(Dispatchers.IO) {
            compressedBitmap.toContentUri(context, generateFileName())
                .onLeft { logger.errorCapturingScreenFromHostApp(it) }
        }
    }

    private suspend fun InputStream.toContentUri(
        context: Context,
        fileName: String
    ): Either<DomainError, Uri> = either {
        val file = context.createContentFile(fileName)
        file.write(this@toContentUri).bind()
        context.getContentUriForFile(file)
    }

    companion object {
        private const val FEEDBACK_FILE_PREFIX: String = "applivery_feedback"
        fun generateFileName(): String =
            "${FEEDBACK_FILE_PREFIX}_${System.currentTimeMillis()}.jpeg"
    }
}

