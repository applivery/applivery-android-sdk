package com.applivery.android.sdk.data.repository.identifier

import android.content.Context
import androidx.core.net.toUri
import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import com.applivery.android.sdk.domain.ensure
import com.applivery.android.sdk.domain.ensureNotNull
import com.applivery.android.sdk.domain.model.DomainError
import java.lang.Long.toHexString

internal class GsfIdProvider(private val context: Context) : MemoizedIdProvider() {

    private val contentResolver get() = context.contentResolver

    override suspend fun getActualDeviceId(): Either<DomainError, DeviceId> = either {
        catch(
            block = {
                val gsfUri = URI_GSF_CONTENT_PROVIDER.toUri()
                val params = arrayOf(ID_KEY)
                val cursor = ensureNotNull(contentResolver.query(gsfUri, null, null, params, null))
                cursor.use { cursor ->
                    ensure(cursor.moveToFirst() && cursor.columnCount >= 2)
                    val gsfId = toHexString(cursor.getString(1).toLong())
                    DeviceId(value = ensureNotNull(gsfId), type = DEVICE_ID_TYPE)
                }
            },
            catch = {
                raise(DeviceIdNotAvailableError(DEVICE_ID_TYPE, it))
            }
        )
    }
}

private const val URI_GSF_CONTENT_PROVIDER = "content://com.google.android.gsf.gservices"
private const val ID_KEY = "android_id"
private const val DEVICE_ID_TYPE = "GSF"
