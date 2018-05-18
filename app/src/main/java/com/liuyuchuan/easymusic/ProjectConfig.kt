package com.liuyuchuan.easymusic


/**
 * Created by Liu Yuchuan on 2018/5/2.
 */
fun ifDebug(f: () -> Unit) {
    if (BuildConfig.DEBUG) {
        f()
    }
}
