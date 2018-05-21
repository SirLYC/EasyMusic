package com.liuyuchuan.easymusic.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.liuyuchuan.easymusic.SORT_DEFAULT
import com.liuyuchuan.easymusic.data.MusicList
import com.liuyuchuan.easymusic.data.Song
import com.liuyuchuan.easymusic.utils.ObservableList

/**
 * Created by Liu Yuchuan on 2018/5/17.
 */
class DBHelper(context: Context) : SQLiteOpenHelper(context, "easyMusic", null, 1) {
    companion object {
        private const val TABLE_SONG = "SONG"
        private const val TABLE_HISTORY = "HISTORY"
        private const val TABLE_LIST = "LIST"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table $TABLE_LIST (" +
                "name text primary key, " +
                "sortMethod integer)")

        db.insert(TABLE_LIST, null, ContentValues().apply {
            put("name", "默认列表")
            put("sortMethod", SORT_DEFAULT)
        })

        db.insert(TABLE_LIST, null, ContentValues().apply {
            put("name", "喜欢")
            put("sortMethod", SORT_DEFAULT)
        })

        db.execSQL("create table $TABLE_SONG (" +
                "name text, " +
                "singer text, " +
                "album text, " +
                "path text, " +
                "duration integer, " +
                "size integer, " +
                "listName text not null references $TABLE_HISTORY(name) on update cascade on delete cascade, " +
                "primary key (path, listName))")

        db.execSQL("create table $TABLE_HISTORY (" +
                "name text, " +
                "singer text, " +
                "album text, " +
                "path text primary key, " +
                "duration integer, " +
                "size integer)")
    }

    fun insertList(name: String, sortMethod: Int = SORT_DEFAULT): Boolean {
        return writableDatabase.insert(TABLE_LIST, null, ContentValues().apply {
            put("name", name)
            put("sortMethod", sortMethod)
        }) != -1L
    }

    fun getSortMethod(listName: String): Int {
        var method = -1
        readableDatabase.query(TABLE_LIST, null, "name=?",
                arrayOf(listName), null, null, null).use { cursor ->
            if (cursor.moveToFirst()) {
                method = cursor.getInt(cursor.getColumnIndex("sortMethod"))
            }
        }

        if (method == -1) {
            // no record
            // with no check success
            insertList(listName)
        }

        if (method < 0 || method > 3) {
            method = 0
        }

        return method
    }

    // try best
    // not assure success
    fun deleteList(name: String): Boolean {
        return writableDatabase.delete(TABLE_LIST, "name=?", arrayOf(name)) != 0
    }

    fun updateSortMethod(name: String, sortMethod: Int): Boolean {
        return writableDatabase.update(TABLE_LIST, ContentValues().apply {
            put("name", name)
            put("sortMethod", sortMethod)
        }, "name=?", arrayOf(name)) != 0
    }

    fun insertSongToList(listName: String, song: Song): Boolean {
        writableDatabase.delete(TABLE_SONG, "path=? and listName=?", arrayOf(song.path, listName))
        return writableDatabase.insert(TABLE_SONG, null, packageSong(song).apply { put("listName", listName) }) != -1L
    }

    fun deleteSong(listName: String, song: Song): Boolean {
        return writableDatabase.delete(TABLE_SONG, "path=? and listName=?", arrayOf(song.path, listName)) != 0
    }

    fun readSongList(): List<MusicList> {
        val songList = mutableListOf<MusicList>()
        val map = hashMapOf<String, Int>()

        readableDatabase.query(TABLE_LIST, null, null, null, null, null, null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val name = cursor.getString(cursor.getColumnIndex("name"))
                    songList.add(MusicList(name, ObservableList(mutableListOf())))
                    map[name] = songList.size - 1
                } while (cursor.moveToNext())
            }
        }

        readableDatabase.query(TABLE_SONG, null, null, null, null, null, null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val song = readSongFromCursor(cursor)
                    val listName = cursor.getString(cursor.getColumnIndex("listName"))
                    val index = map[listName]
                    val list = if (index == null) {
                        MusicList(listName, ObservableList(mutableListOf())).also {
                            songList.add(it)
                            map[listName] = songList.size - 1
                        }.list
                    } else {
                        songList[index].list
                    }

                    list.add(song)
                } while (cursor.moveToNext())
            }
        }

        return songList
    }

    fun deleteHistory(song: Song): Boolean {
        return writableDatabase.delete(TABLE_HISTORY, "path=?", arrayOf(song.path)) != 0
    }

    fun insertHistory(song: Song): Boolean {
        writableDatabase.delete(TABLE_SONG, "path=?", arrayOf(song.path))
        return writableDatabase.insert(TABLE_HISTORY, null, packageSong(song)) != -1L
    }

    fun readHistory(): List<Song> {
        val historyList = mutableListOf<Song>()
        readableDatabase.query(TABLE_HISTORY, null, null, null, null, null, null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    historyList.add(readSongFromCursor(cursor))
                } while (cursor.moveToNext())
            }
        }
        return historyList.apply { reverse() }
    }

    private fun readSongFromCursor(cursor: Cursor): Song {
        val name = cursor.getString(cursor.getColumnIndex("name"))
        val singer = cursor.getString(cursor.getColumnIndex("singer"))
        val album = cursor.getString(cursor.getColumnIndex("album"))
        val path = cursor.getString(cursor.getColumnIndex("path"))
        val duration = cursor.getInt(cursor.getColumnIndex("duration"))
        val size = cursor.getLong(cursor.getColumnIndex("name"))
        return Song(name, singer, album, path, duration, size)
    }

    private fun packageSong(song: Song) = ContentValues().apply {
        put("name", song.name)
        put("singer", song.singer)
        put("album", song.album)
        put("path", song.path)
        put("duration", song.duration)
        put("size", song.size)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // TODO: 2018/5/21 if need
    }
}
