package com.applivery.applvsdklib.tools.executor

interface MainThread {
  fun execute(runnable: Runnable)
}