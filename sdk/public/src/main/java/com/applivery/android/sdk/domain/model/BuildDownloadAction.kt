package com.applivery.android.sdk.domain.model

enum class BuildDownloadAction {
    /**
     * Immediately prompts the user to install the update.
     * This will bring the installation UI to the foreground.
     */
    IMMEDIATE,

    /**
     * Shows a notification when the download is complete.
     * The user can then tap the notification to start the installation at their convenience.
     */
    DEFERRED
}
