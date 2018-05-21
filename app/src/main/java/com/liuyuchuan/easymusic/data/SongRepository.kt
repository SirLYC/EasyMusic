package com.liuyuchuan.easymusic.data

import com.liuyuchuan.easymusic.db.DBHelper
import io.reactivex.Observable

/**
 * Created by Liu Yuchuan on 2018/5/18.
 */
class SongRepository(
        private val dbHelper: DBHelper
) {
    fun getAllList() = Observable.just(dbHelper.readSongList())

    fun getHistory() = Observable.just(dbHelper.readHistory())

    fun createList(listName: String) = Observable.just(dbHelper.insertList(listName))

    fun deleteList(listName: String) = Observable.just(dbHelper.deleteList(listName))

    fun updateSortMethod(listName: String, sortMethod: Int) = Observable.just(dbHelper.updateSortMethod(listName, sortMethod))

    fun addSongTo(listName: String, song: Song) = Observable.just(dbHelper.insertSongToList(listName, song))

    fun deleteSing(listName: String, song: Song) = Observable.just(dbHelper.deleteSong(listName, song))

    fun deleteHistory(song: Song) = Observable.just(dbHelper.deleteHistory(song))

    fun addHistory(song: Song) = Observable.just(dbHelper.insertHistory(song))
}
