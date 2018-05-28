package com.liuyuchuan.easymusic.list

import android.arch.lifecycle.ViewModel
import com.liuyuchuan.easymusic.data.MusicList
import com.liuyuchuan.easymusic.data.Song
import com.liuyuchuan.easymusic.data.SongRepository
import com.liuyuchuan.easymusic.ifDebug
import com.liuyuchuan.easymusic.utils.*
import io.reactivex.Observable
import io.reactivex.internal.disposables.ListCompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by Liu Yuchuan on 2018/5/7.
 */
class SongListViewModel(
        private val musicManager: MusicManager,
        private val songRepository: SongRepository
) : ViewModel() {
    private lateinit var musicList: MusicList

    private val disposables = ListCompositeDisposable()

    val songList = ObservableList(mutableListOf<CheckableItem<Song>>())
    val enableCheckLiveData = NonNullLiveData(false)
    val deleteSongEvent = NonNullSingleLiveEvent<RefreshState>(RefreshState.Empty)

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
        enableCheckLiveData.value = enable
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
        musicManager.playingList.addAll(list.map(CheckableItem<Song>::realItem).filter {
            !musicManager.playingList.contains(it)
        })
    }

    fun updateSortMethod(newMethod: Int) {
        songRepository.updateSortMethod(musicList.name, newMethod)
                .subscribeOn(Schedulers.io())
                .subscribe({ }, {
                    ifDebug {
                        it.printStackTrace()
                    }
                })
    }

    fun deleteSongsFromList(list: List<CheckableItem<Song>>) {
        deleteSongEvent.value.refresh()?.let { nextState ->
            deleteSongEvent.value = nextState
            Observable.fromIterable(list)
                    .async()
                    .concatMapDelayError {
                        songRepository.deleteSong(musicList.name, it.realItem)
                    }
                    .doOnTerminate {
                        songList.removeAll(list)
                        deleteSongEvent.value.result(false)?.let(deleteSongEvent::setValue)
                    }
                    .subscribe({
                        // do  something if need
                    }, {
                        ifDebug {
                            it.printStackTrace()
                        }
                    }).also { disposables.add(it) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
