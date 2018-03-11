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