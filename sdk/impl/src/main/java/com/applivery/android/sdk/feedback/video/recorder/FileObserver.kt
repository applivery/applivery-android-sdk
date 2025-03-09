package com.applivery.android.sdk.feedback.video.recorder

import android.os.FileObserver
import android.os.Handler
import android.os.Looper
import java.io.File
import java.util.Stack

internal class FileObserver(
    private val path: String,
    private val listener: OnReadyListener
) : FileObserver(path, ALL_EVENTS) {

    private val observers: MutableList<SingleFileObserver> = mutableListOf()

    override fun startWatching() {
        val stack = Stack<String>()
        stack.push(path)

        while (!stack.isEmpty()) {
            val parent = stack.pop()
            observers.add(SingleFileObserver(parent, ALL_EVENTS))
            val path = File(parent)
            val files = path.listFiles() ?: continue

            for (f in files) {
                if (f.isDirectory && f.name != "." && f.name != "..") {
                    stack.push(f.path)
                }
            }
        }

        for (sfo in observers) {
            sfo.startWatching()
        }
    }

    override fun stopWatching() {
        for (sfo in observers) {
            sfo.stopWatching()
        }
        observers.clear()
    }

    override fun onEvent(event: Int, path: String?) {
        if (event == CLOSE_WRITE) {
            Handler(Looper.getMainLooper()).post { listener.onFileReady() }
        }
    }

    inner class SingleFileObserver(
        private val path: String,
        mask: Int
    ) : FileObserver(path, mask) {
        override fun onEvent(event: Int, path: String?) {
            val newPath = "${this.path}/$path"
            this@FileObserver.onEvent(event, newPath)
        }
    }

    interface OnReadyListener {
        fun onFileReady()
    }
}
