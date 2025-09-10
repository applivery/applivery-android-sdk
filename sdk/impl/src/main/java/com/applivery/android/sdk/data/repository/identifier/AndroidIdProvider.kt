package com.applivery.android.sdk.data.repository.identifier

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import com.applivery.android.sdk.domain.ensureNotNull
import com.applivery.android.sdk.domain.model.DomainError

internal class AndroidIdProvider(private val context: Context) : MemoizedIdProvider() {

    private val contentResolver get() = context.contentResolver

    @SuppressLint("HardwareIds")
    override suspend fun getActualDeviceId(): Either<DomainError, DeviceId> = either {
        catch(
            block = {
                val id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                DeviceId(value = ensureNotNull(id), type = DEVICE_ID_TYPE)
            },
            catch = { raise(DeviceIdNotAvailableError(DEVICE_ID_TYPE, it)) }
        )
    }
}

private const val DEVICE_ID_TYPE = "Android"