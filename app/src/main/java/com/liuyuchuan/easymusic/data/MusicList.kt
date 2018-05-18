package com.liuyuchuan.easymusic.data

import com.liuyuchuan.easymusic.utils.ObservableList

/**
 * Created by Liu Yuchuan on 2018/5/6.
 */
class MusicList(
        val name: String,
        val list: ObservableList<Song>


) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MusicList

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
