package com.liuyuchuan.easymusic.utils

/**
 * Created by Liu Yuchuan on 2018/5/2.
 */
class CheckableItem<out T>(
        val realItem: T,
        val checkable: Boolean = true
) {

    var isChecked = false
}
