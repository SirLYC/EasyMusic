package com.liuyuchuan.easymusic.data

/**
 * Created by Liu Yuchuan on 2018/5/2.
 */
class Song(
        val name: String,
        val singer: String,
        val album: String,
        val path: String,
        val duration: Int,
        val size: Long
) {

    override fun toString(): String {
        return "Song: (name=$name, singer=$singer, path=$path, duration=$duration, size=$size)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Song) return false

        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }


}
