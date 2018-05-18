package com.liuyuchuan.easymusic.list

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.liuyuchuan.easymusic.data.MusicList
import com.liuyuchuan.easymusic.utils.*

/**
 * Created by Liu Yuchuan on 2018/5/7.
 */
class ListManageViewModel(
        musicManager: MusicManager
) : ViewModel() {
    val selectedMusicListLiveData = MutableLiveData<MusicList>()
    val songListList = ObservableList(mutableListOf<CheckableItem<MusicList>>())
    val enableSelectLiveData = NonNullLiveData<Boolean>(false)

    init {
        selectedMusicListLiveData.value = null
        songListList.add(CheckableItem(musicManager.defaultList, false))
        songListList.add(CheckableItem(musicManager.likeList, false))
        songListList.addAll(musicManager.myLists.map {
            CheckableItem(it)
        })
        SyncChangeListCallback.link(songListList, musicManager.myLists, 2, { it.realItem })
    }

    fun enableCheck(enable: Boolean) {
        enableSelectLiveData.value = enable
    }
}
