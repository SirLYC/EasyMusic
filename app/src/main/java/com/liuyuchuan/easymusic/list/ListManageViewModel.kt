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
        private val musicManager: MusicManager,
        private val songRepository: SongRepository
) : ViewModel() {

    private val disposables = ListCompositeDisposable()
    val selectedMusicListLiveData = MutableLiveData<MusicList>()
    val songListList = ObservableList(mutableListOf<CheckableItem<MusicList>>())
    val enableSelectLiveData = NonNullLiveData(false)

    val listRefreshState = NonNullLiveData<RefreshState>(RefreshState.Empty)
    val listRefreshEvent = NonNullSingleLiveEvent<RefreshState>(RefreshState.Empty)

    init {
        selectedMusicListLiveData.value = null
        listRefreshState.observeForever { listRefreshEvent.value = it!! }
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
                                    CheckableItem(it)
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

    fun enableCheck(enable: Boolean) {
        enableSelectLiveData.value = enable
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}
