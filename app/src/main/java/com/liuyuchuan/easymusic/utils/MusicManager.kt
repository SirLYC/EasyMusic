package com.liuyuchuan.easymusic.utils

import android.content.Context
import android.provider.MediaStore
import com.liuyuchuan.easymusic.data.MusicList
import com.liuyuchuan.easymusic.data.Song
import io.reactivex.Observable


/**
 * Created by Liu Yuchuan on 2018/5/6.
 */
class MusicManager(
        context: Context
) {

    private val appContext = context.applicationContext

    val playingList = ObservableList<Song>(mutableListOf())
    var playPosition = -1

    val musicListList = ObservableList(mutableListOf<MusicList>())

    fun scan(): Observable<List<Song>> {
        return Observable.create<List<Song>> {
            val list = mutableListOf<Song>()
            val cursor = appContext.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null, null, null, MediaStore.Audio.AudioColumns.IS_MUSIC)
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    var name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                    var singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                    val album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                    val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                    val duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                    val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE))
                    if (size > 1000 * 800) {
                        if (name.contains("-")) {
                            val str = name.split("-")
                            if (str.size >= 2) {
                                singer = str[0]
                                name = name.removePrefix("${str[0]}-")
                            }
                        }
                        list.add(Song(name, singer, album, path, duration, size))
                    }
                }

                cursor.close()
            }
            it.onNext(list)
            it.onComplete()
        }
    }
}
