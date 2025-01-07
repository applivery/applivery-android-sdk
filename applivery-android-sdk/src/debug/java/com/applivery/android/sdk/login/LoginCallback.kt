package com.applivery.android.sdk.login

interface LoginCallback {

    fun onLogin()

    fun onCanceled()

    companion object {
        operator fun invoke(onLogin: () -> Unit, onCanceled: () -> Unit = {}): LoginCallback {
            return object : LoginCallback {
                override fun onLogin() = onLogin()
                override fun onCanceled() = onCanceled()
            }
        }
    }
}

object LoginCallbacks : LoginCallback {

    private val callbacks = mutableListOf<LoginCallback>()

    override fun onLogin() {
        callbacks.forEach { it.onLogin() }
        callbacks.clear()
    }

    override fun onCanceled() {
        callbacks.forEach { it.onCanceled() }
        callbacks.clear()
    }

    fun add(callback: LoginCallback) {
        callbacks.add(callback)
    }

    fun remove(callback: LoginCallback) {
        callbacks.remove(callback)
    }
}
