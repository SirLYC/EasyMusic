package com.liuyuchuan.easymusic.data

import com.liuyuchuan.easymusic.db.DBHelper
import io.reactivex.Observable

/**
 * Created by Liu Yuchuan on 2018/5/18.
 */
class SongRepository(
        private val dbHelper: DBHelper
) {
    fun getAllList(): Observable<List<MusicList>> {
        return Observable.just(dbHelper.readSongList())
    }

    fun getHistory(): Observable<List<Song>> {
        return Observable.just(dbHelper.readHistory())
    }

    fun createList(listName: String): Observable<Boolean> {
        return Observable.just(dbHelper.insertList(listName))
    }

    fun deleteList(listName: String): Observable<Boolean> {
        return Observable.just(dbHelper.deleteList(listName))
    }

    fun updateSortMethod(listName: String, sortMethod: Int): Observable<Boolean> {
        return Observable.just(dbHelper.updateSortMethod(listName, sortMethod))
    }

    fun addSongTo(listName: String, song: Song): Observable<Boolean> {
        return Observable.just(dbHelper.insertSongToList(listName, song))
    }

    fun deleteSong(listName: String, song: Song): Observable<Boolean> {
        return Observable.just(dbHelper.deleteSong(listName, song))
    }

    fun deleteHistory(song: Song): Observable<Boolean> {
        return Observable.just(dbHelper.deleteHistory(song))
    }

    fun addHistory(song: Song): Observable<Boolean> {
        return Observable.just(dbHelper.insertHistory(song))
    }
}
