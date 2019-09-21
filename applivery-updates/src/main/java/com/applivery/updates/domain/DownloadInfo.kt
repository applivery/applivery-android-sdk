package com.applivery.updates.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DownloadInfo(
    val filePath: String,
    var progress: Int = 0,
    var currentFileSize: Int = 0,
    var totalFileSize: Int = 0
) : Parcelable