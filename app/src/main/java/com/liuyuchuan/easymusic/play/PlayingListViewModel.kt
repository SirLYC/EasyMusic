package com.liuyuchuan.easymusic.play

import android.arch.lifecycle.ViewModel
import com.liuyuchuan.easymusic.data.Song
import com.liuyuchuan.easymusic.utils.MusicManager

/**
 * Created by Liu Yuchuan on 2018/5/24.
 */
class PlayingListViewModel(
        private val musicManager: MusicManager
) : ViewModel() {

    fun playingList() = musicManager.playingList

    fun chooseSongToPlay(song: Song) {
        musicManager.playingList.clear()
        musicManager.playingList.addAll(musicManager.playingList)
        val index = musicManager.playingList.indexOf(song).let {
            if (it == -1) {
                0
            } else {
                it
            }
        }
        musicManager.playPosition = index
    }
}
