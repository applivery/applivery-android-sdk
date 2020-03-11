package com.applivery.applvsdklib.tools.androidimplementations

import android.annotation.TargetApi
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.File
import java.io.FileInputStream

/**
 * Created by Álvaro Blanco Cabrero on 06/03/2020.
 * applivery-android-sdk.
 */

data class ScreenShotPathInfo(val path: String, val name: String, val addedAt: Long)

/**
 * Maps an [Uri] pointing to a screenshot file image to its info representation
 */
interface ScreenShotPathResolver {

    fun resolvePath(uri: Uri): ScreenShotPathInfo?
}

/**
 * Returns the appropriate [ScreenShotPathResolver] for the caller device's API level
 */
object ScreenShotInfoPathResolverFactory {

    @JvmStatic
    fun get(context: Context): ScreenShotPathResolver =
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> ScreenShotPathResolverApi29(context)
            else -> ScreenShotPathResolverBase(context)
        }
}

/**
 * Base [ScreenShotPathResolver] that queries [MediaStore] to get info about the received [Uri]
 */
@Suppress("DEPRECATION")
open class ScreenShotPathResolverBase(protected val context: Context) : ScreenShotPathResolver {

    override fun resolvePath(uri: Uri): ScreenShotPathInfo? {
        return context.contentResolver.query(
            uri,
            FIELDS,
            null,
            null,
            SORT_ORDER
        )?.use { cursor ->
            runCatching {
                if (cursor.moveToFirst()) {
                    val path =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                    val name =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                    val addedAt =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))
                    ScreenShotPathInfo(path, name, addedAt)
                } else {
                    null
                }
            }.foldNullable()
        }
    }

    companion object {
        private val FIELDS = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_ADDED
        )
        private const val SORT_ORDER = MediaStore.Images.Media.DATE_ADDED + " DESC"
    }
}

/**
 * Specific [ScreenShotPathResolver] for [Build.VERSION_CODES.Q]+ that
 * uses a [java.io.FileDescriptor] to fetch [File] information and copy it to
 * a temporary [File] inside app-specific directory to bypass scoped storage limitation.
 * **More on**: [Android doc](https://developer.android.com/training/data-storage#scoped-storage)
 */
@TargetApi(Build.VERSION_CODES.Q)
class ScreenShotPathResolverApi29(context: Context) : ScreenShotPathResolverBase(context) {

    override fun resolvePath(uri: Uri): ScreenShotPathInfo? {
        return super.resolvePath(uri)?.let { baseInfo ->
            runCatching {
                context.contentResolver.openFileDescriptor(uri, "r", null)?.let { pfd ->
                    val inputStream = FileInputStream(pfd.fileDescriptor)
                    val cacheFile = File(context.cacheDir, baseInfo.name).apply {
                        writeBytes(inputStream.readBytes())
                    }
                    baseInfo.copy(path = cacheFile.absolutePath)
                }
            }.foldNullable()
        }
    }
}

private fun <T> Result<T>.foldNullable(): T? = fold({ it }, { null })
