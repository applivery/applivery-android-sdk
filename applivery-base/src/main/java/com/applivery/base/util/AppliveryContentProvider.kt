package com.applivery.base.util

import android.annotation.SuppressLint
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.PACKAGE_PRIVATE

@VisibleForTesting(otherwise = PACKAGE_PRIVATE)
class AppliveryContentProvider : ContentProvider() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        internal lateinit var context: Context
            private set
    }

    override fun insert(
        uri: Uri,
        values: ContentValues?
    ): Uri? = null

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? = null

    @Suppress("RedundantCompanionReference")
    override fun onCreate(): Boolean {
        Companion.context = checkNotNull(context)
        return true
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ) = 0

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ) = 0

    override fun getType(uri: Uri): String? = null
}