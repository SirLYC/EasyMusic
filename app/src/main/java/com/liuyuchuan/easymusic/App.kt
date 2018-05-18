package com.liuyuchuan.easymusic

import android.app.Application
import com.liuyuchuan.easymusic.utils.BackgroundTaskManager

/**
 * Created by Liu Yuchuan on 2018/5/2.
 */
class App : Application() {
    private lateinit var injection: Injection

    companion object {
        lateinit var INSTANCE: App
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        injection = Injection(this)
        BackgroundTaskManager.start()
    }

    fun injector() = injection
}
