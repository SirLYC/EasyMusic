package com.liuyuchuan.easymusic.utils

import android.support.v7.util.ListUpdateCallback

/**
 * Created by Liu Yuchuan on 2018/5/11.
 */
class SyncChangeListCallback<E, T>(
        private val observableList: ObservableList<E>,
        private val reactList: MutableList<T>,
        private val offset: Int,
        private val map: (E) -> T
) : ListUpdateCallback {

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <E, T> link(observableList: ObservableList<E>, reactList: MutableList<T>, offset: Int = 0, map: (E) -> T = { it as T }) {
            observableList.addCallback(SyncChangeListCallback(
                    observableList, reactList, offset, map
            ))
        }
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {
        reactList[position - offset] = map(observableList[position])
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        reactList[fromPosition - offset] = map(observableList[toPosition])
        reactList[toPosition - offset] = map(observableList[fromPosition])
    }

    override fun onInserted(position: Int, count: Int) {
        var i = 0
        while (i < count) {
            reactList[position - offset + i] = map(observableList[position + i])
            i++
        }
    }

    override fun onRemoved(position: Int, count: Int) {
        var i = 0
        while (i < count) {
            reactList.removeAt(position - offset)
            i++
        }
    }
}
