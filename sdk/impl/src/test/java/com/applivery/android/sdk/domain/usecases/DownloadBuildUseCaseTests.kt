package com.applivery.android.sdk.domain.usecases

import arrow.core.right
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import com.applivery.android.sdk.domain.model.BuildMetadata
import com.applivery.android.sdk.domain.model.NoVersionToUpdateError
import com.applivery.android.sdk.domain.repository.DownloadsRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.util.UUID

class DownloadBuildUseCaseTests {

    @Test
    fun `Given current version greater than build version then NoVersionToUpdateError is returned`() =
        runTest {
            val currentVersion = 2L
            val buildVersion = 1
            val repository = mockk<DownloadsRepository>()
            val hostAppPackageInfoProvider = mockk<HostAppPackageInfoProvider> {
                every { packageInfo } returns mockk {
                    every { versionCode } returns currentVersion
                }
            }

            val useCase = DownloadBuild(
                downloadsRepository = repository,
                hostAppPackageInfoProvider = hostAppPackageInfoProvider
            )

            val result = useCase(
                buildId = UUID.randomUUID().toString(),
                buildVersion = buildVersion
            )
            Assert.assertTrue(result.leftOrNull() is NoVersionToUpdateError)
        }

    @Test
    fun `Given current version lower than build version then build is downloaded`() =
        runTest {
            val currentVersion = 1L
            val buildVersion = 2
            val repository = mockk<DownloadsRepository> {
                coEvery { downloadBuild(any(), any()) } returns File("/").right()
            }
            val hostAppPackageInfoProvider = mockk<HostAppPackageInfoProvider> {
                every { packageInfo } returns mockk {
                    every { versionCode } returns currentVersion
                }
            }

            val useCase = DownloadBuild(
                downloadsRepository = repository,
                hostAppPackageInfoProvider = hostAppPackageInfoProvider
            )

            val result = useCase(
                buildId = UUID.randomUUID().toString(),
                buildVersion = buildVersion
            )
            Assert.assertNotNull(result.getOrNull())
        }

    @Test
    fun `Given any build when is downloaded then returned metadata is as expected`() =
        runTest {
            val buildId = UUID.randomUUID().toString()
            val currentVersion = 1L
            val buildFilePath = "/path/to/build.apk"
            val buildVersion = 2
            val repository = mockk<DownloadsRepository> {
                coEvery { downloadBuild(any(), any()) } returns File(buildFilePath).right()
            }
            val hostAppPackageInfoProvider = mockk<HostAppPackageInfoProvider> {
                every { packageInfo } returns mockk {
                    every { versionCode } returns currentVersion
                }
            }

            val useCase = DownloadBuild(
                downloadsRepository = repository,
                hostAppPackageInfoProvider = hostAppPackageInfoProvider
            )

            val result = useCase(
                buildId = buildId,
                buildVersion = buildVersion
            )
            Assert.assertEquals(
                BuildMetadata(buildId, buildVersion, buildFilePath),
                result.getOrNull()
            )
        }
}