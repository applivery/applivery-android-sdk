package com.applivery.android.sdk.updates

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import com.applivery.android.sdk.AppliveryFileProvider
import com.applivery.android.sdk.domain.model.DomainError
import com.applivery.android.sdk.domain.model.FileWriteError
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

internal suspend fun File.write(stream: InputStream): Either<DomainError, File> = withContext(IO) {
    either {
        catch(
            block = { this@write.apply { stream.use { i -> outputStream().use { o -> i.copyTo(o) } } } },
            catch = { raise(FileWriteError(it.stackTraceToString())) }
        )
    }
}

internal fun Context.createContentFile(fileName: String): File {
    return File(filesDir, fileName)
}

internal fun Context.getContentUriForFile(file: File): Uri {
    return FileProvider.getUriForFile(
        this,
        AppliveryFileProvider.authority,
        file
    )
}