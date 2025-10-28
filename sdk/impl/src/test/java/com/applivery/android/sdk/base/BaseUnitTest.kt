package com.applivery.android.sdk.base

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule

abstract class BaseUnitTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule(testDispatcher = testDispatcher)
}
