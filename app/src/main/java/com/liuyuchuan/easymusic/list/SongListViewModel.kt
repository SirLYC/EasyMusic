package com.liuyuchuan.easymusic.list

import android.arch.lifecycle.ViewModel
import com.liuyuchuan.easymusic.data.MusicList
import com.liuyuchuan.easymusic.data.Song
import com.liuyuchuan.easymusic.utils.CheckableItem
import com.liuyuchuan.easymusic.utils.NonNullLiveData
import com.liuyuchuan.easymusic.utils.ObservableList
import com.liuyuchuan.easymusic.utils.SyncChangeListCallback

/**
 * Created by Liu Yuchuan on 2018/5/7.
 */
class SongListViewModel : ViewModel() {
    private lateinit var musicList: MusicList

    val songList = ObservableList(mutableListOf<CheckableItem<Song>>())
    val enableSelectLiveData = NonNullLiveData<Boolean>(false)

    fun init(musicList: MusicList) {
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
}
