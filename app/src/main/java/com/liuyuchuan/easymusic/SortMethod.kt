package com.liuyuchuan.easymusic

import com.liuyuchuan.easymusic.data.Song
import com.liuyuchuan.easymusic.utils.CheckableItem

/**
 * Created by Liu Yuchuan on 2018/5/18.
 */
const val SORT_DEFAULT = -1
const val SORT_NAME = 0
const val SORT_SINGER = 1
const val SORT_ALBUM = 2

object NameComparator : Comparator<Song> {
    override fun compare(o1: Song, o2: Song): Int {
        return o1.name.compareTo(o2.name)
    }
}

object SingerComparator : Comparator<Song> {
    override fun compare(o1: Song, o2: Song): Int {
        return o1.singer.compareTo(o2.singer)
    }
}

object AlbumComparator : Comparator<Song> {
    override fun compare(o1: Song, o2: Song): Int {
        return o1.album.compareTo(o2.album)
    }
}

object CheckableNameComparator : Comparator<CheckableItem<Song>> {
    override fun compare(o1: CheckableItem<Song>, o2: CheckableItem<Song>): Int {
        return o1.realItem.name.compareTo(o2.realItem.name)
    }
}

object CheckableSingerComparator : Comparator<CheckableItem<Song>> {
    override fun compare(o1: CheckableItem<Song>, o2: CheckableItem<Song>): Int {
        return o1.realItem.singer.compareTo(o2.realItem.singer)
    }
}

object CheckableAlbumComparator : Comparator<CheckableItem<Song>> {
    override fun compare(o1: CheckableItem<Song>, o2: CheckableItem<Song>): Int {
        return o1.realItem.album.compareTo(o2.realItem.album)
    }
}

fun getSongComparator(method: Int): Comparator<Song>? {
    return when (method) {
        SORT_NAME -> NameComparator
        SORT_SINGER -> SingerComparator
        SORT_ALBUM -> AlbumComparator
        else -> null
    }
}

fun getCheckableSongComparator(method: Int): Comparator<CheckableItem<Song>>? {
    return when (method) {
        SORT_NAME -> CheckableNameComparator
        SORT_SINGER -> CheckableSingerComparator
        SORT_ALBUM -> CheckableAlbumComparator
        else -> null
    }
}
