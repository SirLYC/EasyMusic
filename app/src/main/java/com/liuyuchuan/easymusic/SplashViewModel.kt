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
                    }
                    .flatMap {
                        songRepository.getHistory()
                    }
                    .subscribe({
                        // add default list to playing list
                        if (musicManager.playingList.isEmpty()) {
                            musicManager.playingList.addAll(it)
                            if (musicManager.playingList.size > 0) {
                                musicManager.playPosition = 0
                            }
                        }
                    }, {
                        ifDebug {
                            println(it)
                        }
                    })
                    .also { disposables.add(it) }
        }
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}
