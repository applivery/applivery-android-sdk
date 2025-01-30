package com.applivery.android.sdk.updates

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.model.InternalError
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

internal suspend fun File.write(stream: InputStream): Either<DomainError, File> = withContext(IO) {
    either {
        catch(
            block = { this@write.apply { stream.use { i -> outputStream().use { o -> i.copyTo(o) } } } },
            catch = { raise(InternalError()) }
        )
    }
}

internal fun Context.createContentFile(fileName: String): File? {
    return runCatching { File(cacheDir, fileName) }.getOrNull()
}

internal fun Context.getContentUriForFile(file: File): Uri? {
    return runCatching {
        FileProvider.getUriForFile(
            this,
            "$packageName.applivery.fileprovider",
            file
        )
    }.getOrNull()
}