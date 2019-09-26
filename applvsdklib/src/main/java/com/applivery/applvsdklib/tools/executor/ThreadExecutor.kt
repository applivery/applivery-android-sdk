/*
 * Copyright (c) 2019 Applivery
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.applivery.applvsdklib.tools.executor

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


class ThreadExecutor : InteractorExecutor {
  private val threadPoolExecutor: ThreadPoolExecutor

  init {
    threadPoolExecutor = ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
        TIME_UNIT,
        WORK_QUEUE)
  }

  override fun run(interactor: Runnable) {
    threadPoolExecutor.submit(interactor)
  }

  companion object {
    private const val CORE_POOL_SIZE = 3
    private const val MAX_POOL_SIZE = 5
    private const val KEEP_ALIVE_TIME = 120L
    private val TIME_UNIT = TimeUnit.SECONDS
    private val WORK_QUEUE = LinkedBlockingQueue<Runnable>()
  }
}
