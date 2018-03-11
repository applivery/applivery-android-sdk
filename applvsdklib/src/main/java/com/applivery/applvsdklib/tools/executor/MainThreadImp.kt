package com.applivery.applvsdklib.tools.executor

import android.os.Handler
import android.os.Looper


class MainThreadImp : MainThread {

  private val handler: Handler = Handler(Looper.getMainLooper())

  override fun execute(runnable: Runnable) {
    handler.post(runnable)
  }
}