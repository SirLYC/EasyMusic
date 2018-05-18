package com.liuyuchuan.easymusic.utils

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

/**
 * Created by Liu Yuchuan on 2018/5/7.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Fragment.setTitle(title: String) {
    (activity as? AppCompatActivity)?.supportActionBar?.title = title
}
