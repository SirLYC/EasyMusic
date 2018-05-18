package com.liuyuchuan.easymusic.utils

import android.os.Handler
import android.os.HandlerThread

/**
 * Created by Liu Yuchuan on 2018/5/10.
 */
object BackgroundTaskManager {
    private lateinit var backgroundTaskHandler: Handler
    private val handlerThread = HandlerThread("BackgroundTaskManager")

    fun start() {
        handlerThread.start()
        backgroundTaskHandler = Handler(handlerThread.looper)
    }

    // TODO: 2018/5/10 need removing callbacks...
    fun postBackgroundTask(r: Runnable) {
        backgroundTaskHandler.post(r)
    }
}
