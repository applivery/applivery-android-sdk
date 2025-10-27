package com.applivery.android.sdk.domain.usecases

import arrow.core.left
import arrow.core.right
import com.applivery.android.sdk.HostActivityProvider
import com.applivery.android.sdk.domain.DomainLogger
import com.applivery.android.sdk.domain.HostAppPackageInfoProvider
import com.applivery.android.sdk.domain.PostponedUpdateLogic
import com.applivery.android.sdk.domain.ScreenRouter
import com.applivery.android.sdk.domain.model.AppConfig
import com.applivery.android.sdk.domain.model.UnauthorizedError
import com.applivery.android.sdk.domain.repository.AppliveryRepository
import com.applivery.android.sdk.fakes.FakeLogger
import io.mockk.Called
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.UUID

class CheckUpdatesUseCaseTests {

    @Test
    fun `Given no config available then no more interactions are made`() = runTest {
        val repository = mockk<AppliveryRepository> {
            coEvery { getConfig() } returns UnauthorizedError().left()
        }
        val screenRouter = mockk<ScreenRouter>()
        val hostActivityProvider = mockk<HostActivityProvider>()
        val logger = DomainLogger(FakeLogger())
        val postponedUpdateLogic = mockk<PostponedUpdateLogic>()
        val hostAppPackageInfoProvider = mockk<HostAppPackageInfoProvider>()

        val checkUpdatesUseCase = CheckUpdates(
            repository = repository,
            screenRouter = screenRouter,
            logger = logger,
            postponedUpdateLogic = postponedUpdateLogic,
            hostAppPackageInfoProvider = hostAppPackageInfoProvider
        )

        checkUpdatesUseCase(forceUpdate = false)
        val dependencies = listOf(
            hostActivityProvider,
            postponedUpdateLogic,
            hostAppPackageInfoProvider
        )
        coVerify { dependencies wasNot Called }
    }

    @Test
    fun `Given no update available then no more interactions are made`() = runTest {
        val config = AppConfig(
            ota = false,
            forceUpdate = false,
            lastBuildVersion = 1,
            minVersion = 1,
            lastBuildId = UUID.randomUUID().toString(),
            forceAuth = false
        )
        val currentAppVersion = 2L
        val repository = mockk<AppliveryRepository> {
            coEvery { getConfig() } returns config.right()
        }
        val screenRouter = mockk<ScreenRouter>()
        val hostActivityProvider = mockk<HostActivityProvider>()
        val logger = DomainLogger(FakeLogger())
        val postponedUpdateLogic = mockk<PostponedUpdateLogic>()
        val hostAppPackageInfoProvider = mockk<HostAppPackageInfoProvider> {
            coEvery { packageInfo } returns mockk {
                coEvery { versionCode } returns currentAppVersion
            }
        }

        val checkUpdatesUseCase = CheckUpdates(
            repository = repository,
            screenRouter = screenRouter,
            logger = logger,
            postponedUpdateLogic = postponedUpdateLogic,
            hostAppPackageInfoProvider = hostAppPackageInfoProvider
        )

        checkUpdatesUseCase(forceUpdate = false)
        val dependencies = listOf(
            hostActivityProvider,
            postponedUpdateLogic,
        )
        coVerify { dependencies wasNot Called }
    }

    @Test
    fun `Given update available and forceUpdate enabled then force update screen is launched`() =
        runTest {
            val config = AppConfig(
                ota = true,
                forceUpdate = true,
                lastBuildVersion = 2,
                minVersion = 2,
                lastBuildId = UUID.randomUUID().toString(),
                forceAuth = false
            )
            val currentAppVersion = 1L
            val repository = mockk<AppliveryRepository> {
                coEvery { getConfig() } returns config.right()
            }
            val screenRouter = mockk<ScreenRouter> {
                every { navigateToForceUpdateScreen() } just Runs
            }
            val logger = DomainLogger(FakeLogger())
            val postponedUpdateLogic = mockk<PostponedUpdateLogic>()
            val hostAppPackageInfoProvider = mockk<HostAppPackageInfoProvider> {
                coEvery { packageInfo } returns mockk {
                    coEvery { versionCode } returns currentAppVersion
                }
            }

            val checkUpdatesUseCase = CheckUpdates(
                repository = repository,
                screenRouter = screenRouter,
                logger = logger,
                postponedUpdateLogic = postponedUpdateLogic,
                hostAppPackageInfoProvider = hostAppPackageInfoProvider
            )

            checkUpdatesUseCase(forceUpdate = false)

            verify { screenRouter.navigateToForceUpdateScreen() }
        }

    @Test
    fun `Given update available and but no OTA enabled then no screen is launched`() =
        runTest {
            val config = AppConfig(
                ota = false,
                forceUpdate = false,
                lastBuildVersion = 2,
                minVersion = 1,
                lastBuildId = UUID.randomUUID().toString(),
                forceAuth = false
            )
            val currentAppVersion = 1L
            val repository = mockk<AppliveryRepository> {
                coEvery { getConfig() } returns config.right()
            }
            val screenRouter = mockk<ScreenRouter>()
            val logger = DomainLogger(FakeLogger())
            val postponedUpdateLogic = mockk<PostponedUpdateLogic>()
            val hostAppPackageInfoProvider = mockk<HostAppPackageInfoProvider> {
                coEvery { packageInfo } returns mockk {
                    coEvery { versionCode } returns currentAppVersion
                }
            }

            val checkUpdatesUseCase = CheckUpdates(
                repository = repository,
                screenRouter = screenRouter,
                logger = logger,
                postponedUpdateLogic = postponedUpdateLogic,
                hostAppPackageInfoProvider = hostAppPackageInfoProvider
            )

            checkUpdatesUseCase(forceUpdate = false)

            verify { screenRouter wasNot Called }
        }

    @Test
    fun `Given update available then suggested update screen is launched`() =
        runTest {
            val config = AppConfig(
                ota = true,
                forceUpdate = false,
                lastBuildVersion = 2,
                minVersion = 1,
                lastBuildId = UUID.randomUUID().toString(),
                forceAuth = false
            )
            val currentAppVersion = 1L
            val repository = mockk<AppliveryRepository> {
                coEvery { getConfig() } returns config.right()
            }
            val screenRouter = mockk<ScreenRouter> {
                every { navigateToSuggestedUpdateScreen() } just Runs
            }
            val logger = DomainLogger(FakeLogger())
            val postponedUpdateLogic = mockk<PostponedUpdateLogic> {
                every { isUpdatePostponed() } returns false
            }
            val hostAppPackageInfoProvider = mockk<HostAppPackageInfoProvider> {
                coEvery { packageInfo } returns mockk {
                    coEvery { versionCode } returns currentAppVersion
                }
            }

            val checkUpdatesUseCase = CheckUpdates(
                repository = repository,
                screenRouter = screenRouter,
                logger = logger,
                postponedUpdateLogic = postponedUpdateLogic,
                hostAppPackageInfoProvider = hostAppPackageInfoProvider
            )

            checkUpdatesUseCase(forceUpdate = false)

            verify { screenRouter.navigateToSuggestedUpdateScreen() }
        }

    @Test
    fun `Given update available but postponed update then no screen is launched`() =
        runTest {
            val isUpdatePostponed = true
            val config = AppConfig(
                ota = true,
                forceUpdate = false,
                lastBuildVersion = 2,
                minVersion = 1,
                lastBuildId = UUID.randomUUID().toString(),
                forceAuth = false
            )
            val currentAppVersion = 1L
            val repository = mockk<AppliveryRepository> {
                coEvery { getConfig() } returns config.right()
            }
            val screenRouter = mockk<ScreenRouter>()
            val logger = DomainLogger(FakeLogger())
            val postponedUpdateLogic = mockk<PostponedUpdateLogic> {
                every { isUpdatePostponed() } returns isUpdatePostponed
            }
            val hostAppPackageInfoProvider = mockk<HostAppPackageInfoProvider> {
                coEvery { packageInfo } returns mockk {
                    coEvery { versionCode } returns currentAppVersion
                }
            }

            val checkUpdatesUseCase = CheckUpdates(
                repository = repository,
                screenRouter = screenRouter,
                logger = logger,
                postponedUpdateLogic = postponedUpdateLogic,
                hostAppPackageInfoProvider = hostAppPackageInfoProvider
            )

            checkUpdatesUseCase(forceUpdate = false)

            verify { screenRouter wasNot Called }
        }

    @Test
    fun `Given update available and postponed but forceUpdate enabled then suggested update screen is launched`() =
        runTest {
            val isUpdatePostponed = true
            val config = AppConfig(
                ota = true,
                forceUpdate = false,
                lastBuildVersion = 2,
                minVersion = 1,
                lastBuildId = UUID.randomUUID().toString(),
                forceAuth = false
            )
            val currentAppVersion = 1L
            val repository = mockk<AppliveryRepository> {
                coEvery { getConfig() } returns config.right()
            }
            val screenRouter = mockk<ScreenRouter> {
                every { navigateToSuggestedUpdateScreen() } just Runs
            }
            val logger = DomainLogger(FakeLogger())
            val postponedUpdateLogic = mockk<PostponedUpdateLogic> {
                every { isUpdatePostponed() } returns isUpdatePostponed
            }
            val hostAppPackageInfoProvider = mockk<HostAppPackageInfoProvider> {
                coEvery { packageInfo } returns mockk {
                    coEvery { versionCode } returns currentAppVersion
                }
            }

            val checkUpdatesUseCase = CheckUpdates(
                repository = repository,
                screenRouter = screenRouter,
                logger = logger,
                postponedUpdateLogic = postponedUpdateLogic,
                hostAppPackageInfoProvider = hostAppPackageInfoProvider
            )

            checkUpdatesUseCase(forceUpdate = true)

            verify { screenRouter.navigateToSuggestedUpdateScreen() }
        }
}