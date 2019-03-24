package com.applivery.applvsdklib

interface BindUserCallback {

  fun onSuccess()

  fun onError(message: String)
}