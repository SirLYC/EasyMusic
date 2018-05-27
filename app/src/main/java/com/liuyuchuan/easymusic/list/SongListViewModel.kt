package com.liuyuchuan.easymusic.list

import android.arch.lifecycle.ViewModel
import com.liuyuchuan.easymusic.data.MusicList
import com.liuyuchuan.easymusic.data.Song
import com.liuyuchuan.easymusic.utils.*

/**
 * Created by Liu Yuchuan on 2018/5/7.
 */
class SongListViewModel(
        private val musicManager: MusicManager
) : ViewModel() {
    private lateinit var musicList: MusicList

    val songList = ObservableList(mutableListOf<CheckableItem<Song>>())
    val enableSelectLiveData = NonNullLiveData(false)

    fun init(musicList: MusicList) {
        songList.removeAllCallbacks()
        songList.clear()
        this.musicList = musicList
        songList.addAll(musicList.list.map {
            CheckableItem(it)
        })

        SyncChangeListCallback.link(songList, musicList.list, map = { it.realItem })
    }

    fun enableCheck(enable: Boolean) {
        enableSelectLiveData.value = enable
    }

    fun chooseSongToPlay(song: Song) {
        musicManager.playingList.clear()
        musicManager.playingList.addAll(musicList.list)
        val index = musicManager.playingList.indexOf(song).let {
            if (it == -1) {
                0
            } else {
                it
            }
        }
        musicManager.playPosition = index
    }

    fun addListToPlayList(list: List<CheckableItem<Song>>) {
        musicManager.playingList.addAll(list.map(CheckableItem<Song>::realItem))
    }

//    fun addSongToPlayList(song: Song) {
//        musicManager.playingList.add(song)
//    }
}
