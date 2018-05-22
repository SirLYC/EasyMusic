package com.liuyuchuan.easymusic

import android.arch.lifecycle.ViewModel
import com.liuyuchuan.easymusic.data.SongRepository
import com.liuyuchuan.easymusic.utils.MusicManager
import com.liuyuchuan.easymusic.utils.NonNullLiveData
import com.liuyuchuan.easymusic.utils.RefreshState
import com.liuyuchuan.easymusic.utils.async
import io.reactivex.Observable
import io.reactivex.internal.disposables.ListCompositeDisposable

/**
 * Created by Liu Yuchuan on 2018/5/16.
 */
class SplashViewModel(
        private val musicManager: MusicManager,
        private val songRepository: SongRepository
) : ViewModel() {
    val scanRefreshState = NonNullLiveData<RefreshState>(RefreshState.Empty)
    private val disposables = ListCompositeDisposable()

    init {
    }

    fun scan() {
        scanRefreshState.value.refresh()?.let { nextState ->
            scanRefreshState.value = nextState
            musicManager.scan()
                    .async()
                    .flatMap {
                        Observable.fromIterable(it)
                    }
                    .doOnTerminate { scanRefreshState.value = RefreshState.NotEmpty }
                    .concatMapDelayError {
                        songRepository.addSongTo("默认列表", it)
                    }.subscribe({ }, {
                        ifDebug {
                            println(it)
                        }
                    })
                    .also { disposables.add(it) }
        }
    }

    fun readHistory() {
        songRepository.getHistory()
                .async()
                .subscribe({
                    musicManager.historyList.addAll(it)
                }, {
                    ifDebug {
                        println(it)
                    }
                })
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}
