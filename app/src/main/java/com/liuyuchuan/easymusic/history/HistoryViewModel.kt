package com.liuyuchuan.easymusic.history

import android.arch.lifecycle.ViewModel
import com.liuyuchuan.easymusic.data.Song
import com.liuyuchuan.easymusic.data.SongRepository
import com.liuyuchuan.easymusic.ifDebug
import com.liuyuchuan.easymusic.utils.*
import io.reactivex.internal.disposables.ListCompositeDisposable

/**
 * Created by Liu Yuchuan on 2018/5/24.
 */
class HistoryViewModel(
        private val songRepository: SongRepository,
        private val musicManager: MusicManager
) : ViewModel() {
    private val disposables = ListCompositeDisposable()

    val refreshState = NonNullLiveData<RefreshState>(RefreshState.Empty)
    val refreshEvent = NonNullSingleLiveEvent<RefreshState>(RefreshState.Empty)

    val historyList = ObservableList(mutableListOf<Song>())

    init {
        refreshState.observeForever { refreshEvent.value = it!! }
    }

    fun getHistoryList() {
        refreshState.value.refresh()?.let { nextState ->
            refreshState.value = nextState
            songRepository.getHistory()
                    .async()
                    .subscribe({ list ->

                        refreshState.value.result(list.isEmpty())?.let {
                            if (list.isNotEmpty()) {
                                historyList.clear()
                                historyList.addAll(list)
                            }
                            refreshState.value = it
                        }

                    }, {
                        ifDebug {
                            println(it)
                        }

                        refreshState.value.error("拉取历史记录失败")?.let {
                            refreshState.value = it
                        }
                    })
                    .also { disposables.add(it) }

        }
    }

    fun chooseSongToPlay(song: Song) {
        musicManager.playingList.clear()
        musicManager.playingList.addAll(historyList)
        val index = musicManager.playingList.indexOf(song).let {
            if (it == -1) {
                0
            } else {
                it
            }
        }
        musicManager.playPosition = index
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}
