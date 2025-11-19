package com.applivery.android.sdk.updates

import android.content.Context
import arrow.core.Either
import arrow.core.left
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.right
import com.applivery.android.sdk.domain.model.AppUpdateError.Installation
import com.applivery.android.sdk.domain.model.AppUpdateError.ReadBuildFile
import com.applivery.android.sdk.domain.model.DomainError
import ru.solrudev.ackpine.installer.InstallFailure
import ru.solrudev.ackpine.installer.PackageInstaller
import ru.solrudev.ackpine.installer.createSession
import ru.solrudev.ackpine.installer.parameters.InstallerType
import ru.solrudev.ackpine.session.Failure
import ru.solrudev.ackpine.session.Session
import ru.solrudev.ackpine.session.await
import ru.solrudev.ackpine.session.parameters.Confirmation
import java.io.File

internal interface BuildInstaller {

    suspend fun install(file: File): Either<DomainError, Unit>
}

internal class AndroidBuildInstaller(private val context: Context) : BuildInstaller {

    override suspend fun install(file: File): Either<DomainError, Unit> = either {

        val contentUri = catch(
            block = { context.getContentUriForFile(file) },
            catch = { raise(ReadBuildFile(it.stackTraceToString())) }
        )
        val packageInstaller = PackageInstaller.getInstance(context)
        val session = packageInstaller.createSession(contentUri) {
            confirmation = Confirmation.IMMEDIATE
            installerType = InstallerType.INTENT_BASED
        }
        return when (val result = session.await()) {
            is Session.State.Failed<*> -> Installation(
                result.failure.toInstallationCause(),
                result.failure.message()
            ).left()

            is Session.State.Succeeded -> Unit.right()
        }
    }

    private fun Failure.message(): String {
        return when (this) {
            is InstallFailure.Aborted -> "Aborted"
            is InstallFailure.Blocked -> "Blocked by $otherPackageName"
            is InstallFailure.Conflict -> "Conflicting with $otherPackageName"
            is InstallFailure.Exceptional -> exception.stackTraceToString()
            is InstallFailure.Generic -> "Generic: $message"
            is InstallFailure.Incompatible -> "Incompatible"
            is InstallFailure.Invalid -> "Invalid"
            is InstallFailure.Storage -> "Storage path: $storagePath"
            is InstallFailure.Timeout -> "Timeout"
            else -> "Unknown error"
        }
    }

    private fun Failure.toInstallationCause(): Installation.Cause {
        return when (this) {
            is InstallFailure.Storage -> Installation.Cause.InsufficientStorage
            else -> Installation.Cause.Unknown
        }
    }
}
