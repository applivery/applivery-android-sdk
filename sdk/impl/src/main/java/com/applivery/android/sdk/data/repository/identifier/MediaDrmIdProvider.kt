package com.applivery.android.sdk.data.repository.identifier


import android.media.MediaDrm
import android.os.Build
import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import com.applivery.android.sdk.domain.ensureNotNull
import com.applivery.android.sdk.domain.model.DomainError
import java.security.MessageDigest
import java.util.UUID


internal class MediaDrmIdProvider : MemoizedIdProvider() {

    override suspend fun getActualDeviceId(): Either<DomainError, DeviceId> = either {
        catch(
            block = {
                val widevineUUID = UUID(WIDEWINE_UUID_MOST_SIG_BITS, WIDEWINE_UUID_LEAST_SIG_BITS)
                val drm = MediaDrm(widevineUUID)
                val drmDeviceIdBytes = drm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    drm.close()
                } else {
                    @Suppress("DEPRECATION")
                    drm.release()
                }
                val messageDigest = MessageDigest.getInstance("SHA-256").apply {
                    update(drmDeviceIdBytes)
                }
                val drmId = messageDigest.digest().toHexString()
                DeviceId(value = ensureNotNull(drmId), type = DEVICE_ID_TYPE)
            },
            catch = {
                raise(DeviceIdNotAvailableError(DEVICE_ID_TYPE, it))
            }
        )
    }
}

private fun ByteArray.toHexString(): String {
    return this.joinToString("") {
        java.lang.String.format("%02x", it)
    }
}

private const val WIDEWINE_UUID_MOST_SIG_BITS = -0x121074568629b532L
private const val WIDEWINE_UUID_LEAST_SIG_BITS = -0x5c37d8232ae2de13L
private const val DEVICE_ID_TYPE = "MediaDrm"