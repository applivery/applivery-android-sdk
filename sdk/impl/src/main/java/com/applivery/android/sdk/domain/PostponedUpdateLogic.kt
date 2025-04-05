package com.applivery.android.sdk.domain

import androidx.core.content.edit
import kotlin.time.Duration

interface PostponedUpdateLogic {

    fun onUpdatePostponedFor(duration: Duration)

    fun isUpdatePostponed(): Boolean
}

class PostponedUpdateLogicImpl(
    private val sharedPreferencesProvider: SharedPreferencesProvider
) : PostponedUpdateLogic {

    private val preferences get() = sharedPreferencesProvider.sharedPreferences

    override fun onUpdatePostponedFor(duration: Duration) {
        val updatePostponedUntil = System.currentTimeMillis() + duration.inWholeMilliseconds
        preferences.edit { putLong(KeyPostponedUpdate, updatePostponedUntil) }
    }

    override fun isUpdatePostponed(): Boolean {
        val updatePostponedUntil = preferences.getLong(KeyPostponedUpdate, 0)
        return updatePostponedUntil > System.currentTimeMillis()
    }

    companion object {
        private const val KeyPostponedUpdate = "preferences:postponed_update"
    }
}