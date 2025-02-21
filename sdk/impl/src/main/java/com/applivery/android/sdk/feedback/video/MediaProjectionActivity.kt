package com.applivery.android.sdk.feedback.video

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.getSystemService
import com.applivery.android.sdk.SdkBaseActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

internal class MediaProjectionActivity : SdkBaseActivity() {

    private val mediaProjectionManager by lazy { application.getSystemService<MediaProjectionManager>() }
    private val contract = ActivityResultContracts.StartActivityForResult()
    private var mediaProjectionLauncher = registerForActivityResult(contract) {
        MediaProjectionCallbacks.onResult(it)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = mediaProjectionManager?.createScreenCaptureIntent() ?: return
        mediaProjectionLauncher.launch(intent)
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, MediaProjectionActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
    }
}

internal suspend fun Activity.requestMediaPermission(): ActivityResult {
    return suspendCancellableCoroutine { cont ->
        MediaProjectionCallbacks.add { cont.resume(it) }
        startActivity(MediaProjectionActivity.getIntent(this))
    }
}
