package com.liuyuchuan.easymusic.utils

import android.content.Context
import android.provider.MediaStore
import android.support.annotation.WorkerThread
import com.liuyuchuan.easymusic.data.MusicList
import com.liuyuchuan.easymusic.data.Song
import io.reactivex.Observable
import java.io.File


/**
 * Created by Liu Yuchuan on 2018/5/6.
 */
class MusicManager(
        context: Context
) {

    companion object {
        private const val NAME_DEFAULT = "default"
        private const val NAME_LIKE = "like"
        private const val NAME_DIR = "list"
        private const val INDEX_DEFAULT = 0
        private const val INDEX_LIKE = 1
    }

    private val appContext = context.applicationContext

    val playingList = ObservableList(mutableListOf<Song>())
    val historyList = ObservableList(mutableListOf<Song>())

    val defaultList = MusicList(NAME_DEFAULT, ObservableList(mutableListOf()))
    val likeList = MusicList(NAME_LIKE, ObservableList(mutableListOf()))
    val myLists = ObservableList(mutableListOf<MusicList>())


    private fun savedDirectory(editable: Boolean = false) = appContext.cacheDir.let {
        File(it, NAME_DIR)
    }.checkDir()?.let {
        if (!editable) {
            it
        } else {
            File(it, "myLists")
        }
    }

    private fun savedTargetFile(musicList: MusicList) = savedDirectory(true)?.let {
        File(it, musicList.name)
    }

    // work on worker thread
    // try best to cache the list
    // cannot assure 100% saved
    @WorkerThread
    @Synchronized
    fun saveList(musicList: MusicList) {
        savedTargetFile(musicList)?.writeJson(musicList)
    }

    fun readList(readDefault: Boolean): Observable<Map<String, Boolean>> {
        return Observable.create<Map<String, Boolean>> {
            val map = hashMapOf<String, Boolean>()

            if (readDefault) {
                val localDefaultList = savedTargetFile(defaultList)?.readJson(MusicList::class.java)

                if (localDefaultList != null) {
                    defaultList.list.clear()
                    defaultList.list.addAll(localDefaultList.list)
                }
            }

            val localLikeList = savedTargetFile(likeList)?.readJson(MusicList::class.java)

            if (localLikeList == null) {
                map[NAME_LIKE] = false
            } else {
                map[NAME_LIKE] = true
                likeList.list.clear()
                likeList.list.addAll(localLikeList.list)
            }

            savedDirectory(true)?.listFiles { f ->
                val musicList = f.readJson(MusicList::class.java)
                if (musicList == null) {
                    map[f.name] = false
                    false
                } else {
                    map[musicList.name] = true
                    myLists.add(musicList)
                    true
                }
            }

            it.onNext(map)
            it.onComplete()
        }
    }

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
