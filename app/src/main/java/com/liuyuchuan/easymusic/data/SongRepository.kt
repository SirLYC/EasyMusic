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
}
