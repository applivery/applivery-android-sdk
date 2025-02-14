package com.applivery.android.sdk.feedback

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.applivery.android.sdk.domain.DomainLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal interface ContentUriImageDecoder {
    suspend fun of(uri: Uri): Bitmap?
}

internal class ContentUriImageDecoderImpl(
    context: Context,
    private val domainLogger: DomainLogger
) : ContentUriImageDecoder {

    private val contentResolver = context.contentResolver

    override suspend fun of(uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }
        }.onFailure { domainLogger.imageDecodingFailed(it) }.getOrNull()
    }
}