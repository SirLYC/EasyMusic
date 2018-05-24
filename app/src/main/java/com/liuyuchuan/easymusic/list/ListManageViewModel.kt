package com.liuyuchuan.easymusic.list

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.liuyuchuan.easymusic.data.MusicList
import com.liuyuchuan.easymusic.data.SongRepository
import com.liuyuchuan.easymusic.ifDebug
import com.liuyuchuan.easymusic.utils.*
import io.reactivex.internal.disposables.ListCompositeDisposable

/**
 * Created by Liu Yuchuan on 2018/5/7.
 */
class ListManageViewModel(
        musicManager: MusicManager,
        private val songRepository: SongRepository
) : ViewModel() {

    private val disposables = ListCompositeDisposable()
    val selectedMusicListLiveData = MutableLiveData<MusicList>()
    val songListList = ObservableList(mutableListOf<CheckableItem<MusicList>>())
    val enableSelectLiveData = NonNullLiveData(false)

    val listRefreshState = NonNullLiveData<RefreshState>(RefreshState.Empty)
    val listRefreshEvent = NonNullSingleLiveEvent<RefreshState>(RefreshState.Empty)

    val addListRefreshState = NonNullLiveData<RefreshState>(RefreshState.Empty)
    val addListRefreshEvent = NonNullSingleLiveEvent<RefreshState>(RefreshState.Empty)

    init {
        SyncChangeListCallback.link(songListList, musicManager.musicListList, map = { it -> it.realItem })
        selectedMusicListLiveData.value = null
        listRefreshState.observeForever { listRefreshEvent.value = it!! }
        addListRefreshState.observeForever { addListRefreshEvent.value = it!! }
    }

    fun refreshList() {
        listRefreshState.value.refresh()?.let { nextState ->
            listRefreshState.value = nextState
            songRepository.getAllList()
                    .async()
                    .subscribe({ list ->
                        listRefreshState.value.result(list.isEmpty())?.let {
                            if (list.isNotEmpty()) {
                                songListList.clear()
                                songListList.addAll(list.map {
                                    if (it.name == "默认列表" || it.name == "喜欢") {
                                        CheckableItem(it, false)
                                    } else {
                                        CheckableItem(it)
                                    }
                                })
                            }
                            listRefreshState.value = it
                        }
                    }, {
                        ifDebug {
                            it.printStackTrace()
                        }

                        listRefreshState.value.error("获取列表失败")?.let(listRefreshState::setValue)
                    })
                    .also { disposables.add(it) }
        }
    }

    fun addSongList(name: String) {
        addListRefreshState.value.refresh()?.let { nextState ->
            addListRefreshState.value = nextState
            songRepository.createList(name)
                    .async()
                    .subscribe({
                        if (it) {
                            addListRefreshState.value.result(false)?.let {
                                songListList.add(CheckableItem(MusicList(name, ObservableList(mutableListOf()))))
                                addListRefreshState.value = it
                                addListRefreshState.value = RefreshState.Empty
                            }
                        } else {
                            addListRefreshState.value.error("创建列表失败")?.let(addListRefreshState::setValue)
                        }
                    }, {
                        ifDebug {
                            it.printStackTrace()
                        }
                        addListRefreshState.value.error("创建列表出错")?.let(addListRefreshState::setValue)
                    })
                    .also { disposables.add(it) }
        }
    }

    fun enableCheck(enable: Boolean) {
        enableSelectLiveData.value = enable
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}
