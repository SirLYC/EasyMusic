package com.liuyuchuan.easymusic.play

import android.arch.lifecycle.ViewModel
import com.liuyuchuan.easymusic.data.Song
import com.liuyuchuan.easymusic.utils.*

/**
 * Created by Liu Yuchuan on 2018/5/24.
 */
class PlayingListViewModel(
        private val musicManager: MusicManager
) : ViewModel() {

    val playList = ObservableList(mutableListOf<CheckableItem<Song>>())
    val enableCheckLiveData = NonNullLiveData(false)

    fun init() {
        playList.clear()
        playList.addAll(musicManager.playingList.map {
            CheckableItem(it)
        })
        playList.removeAllCallbacks()
        SyncChangeListCallback.link(playList, musicManager.playingList, map = { it.realItem })
    }

    fun chooseSongToPlay(song: Song) {
        val index = musicManager.playingList.indexOf(song).let {
            if (it == -1) {
                0
            } else {
                it
            }
        }
        musicManager.playPosition = index
    }

    fun enableCheck(enable: Boolean) {
        enableCheckLiveData.value = enable
    }
}
