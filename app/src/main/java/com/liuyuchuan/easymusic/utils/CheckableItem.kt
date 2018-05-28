package com.liuyuchuan.easymusic.utils

/**
 * Created by Liu Yuchuan on 2018/5/2.
 */
class CheckableItem<out T>(
        val realItem: T,
        val checkable: Boolean = true
) {

    var isChecked = false
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CheckableItem<*>

        if (realItem != other.realItem) return false

        return true
    }

    override fun hashCode(): Int {
        return realItem?.hashCode() ?: 0
    }


}
