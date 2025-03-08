package com.applivery.android.sdk.feedback

import android.graphics.Bitmap
import android.net.Uri

internal sealed interface FeedbackAttachment {
    data class Screenshot(val screenshot: Bitmap) : FeedbackAttachment
    data class Video(val uri: Uri) : FeedbackAttachment
}
