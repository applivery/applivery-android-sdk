package com.applivery.android.sdk.domain.model

internal abstract class DomainError(val message: String? = null)

internal abstract class DeveloperError : DomainError()

internal class UnauthorizedError : DomainError()
internal class LimitExceededError : DeveloperError()
internal class SubscriptionError : DeveloperError()
internal class InternalError(message: String? = null) : DomainError(message)
internal class FileWriteError(message: String? = null) : DomainError(message)

internal class NoVersionToUpdateError : DomainError()

internal sealed class AppUpdateError(message: String? = null) : DomainError(message) {
    class DownloadBuild(message: String?) : AppUpdateError(message)
    class CreateBuildFile(message: String?) : AppUpdateError(message)
    class ReadBuildFile(message: String?) : AppUpdateError(message)
    class Installation(val cause: Cause, message: String?) : AppUpdateError(message) {
        internal enum class Cause {
            InsufficientStorage,
            Unknown,
        }
    }
}
