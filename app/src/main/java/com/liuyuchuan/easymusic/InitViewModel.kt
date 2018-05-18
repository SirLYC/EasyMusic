package com.liuyuchuan.easymusic

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.liuyuchuan.easymusic.utils.*
import io.reactivex.internal.disposables.ListCompositeDisposable

/**
 * Created by Liu Yuchuan on 2018/5/16.
 */
class InitViewModel(
        private val musicManager: MusicManager
) : ViewModel() {
    private val disposables = ListCompositeDisposable()

    val scanRefreshEvent = NonNullSingleLiveEvent<RefreshState>(RefreshState.Empty)
    val scanRefreshState = NonNullLiveData<RefreshState>(RefreshState.Empty)

    val initRefreshEvent = NonNullSingleLiveEvent<RefreshState>(RefreshState.Empty)
    val initRefreshState = NonNullLiveData<RefreshState>(RefreshState.Empty)

    init {
        scanRefreshState.observeForever { scanRefreshEvent.value = it!! }
        initRefreshState.observeForever { initRefreshEvent.value = it!! }
    }

    fun scan() {
        scanRefreshState.value.refresh()?.let { nextState ->
            scanRefreshState.value = nextState
            musicManager.scan()
                    .async()
                    .subscribe({ list ->
                        scanRefreshState.value.result(false)?.let { result ->
                            if (list.isNotEmpty()) {
                                musicManager.defaultList.list.clear()
                                musicManager.defaultList.list.addAll(list)

                                // save in background thread
                                BackgroundTaskManager.postBackgroundTask(Runnable {
                                    musicManager.run {
                                        saveList(defaultList)
                                    }
                                })
                            }

                            ifDebug {
                                Log.d("InitViewModel", "scan success\n$list")
                            }
                            scanRefreshState.value = result
                        }
                    }, {
                        scanRefreshState.value.error("扫描失败")?.let(scanRefreshState::setValue)
                        ifDebug { println(it) }
                    }).also { disposables.add(it) }
        }
    }

    fun init(readDefault: Boolean) {
        initRefreshState.value.refresh()?.let { nextState ->
            initRefreshState.value = nextState
            musicManager.readList(readDefault)
                    .async()
                    .subscribe({
                        // TODO: 2018/5/16 may use map's info

                        initRefreshState.value.result(false)?.let(initRefreshState::setValue)

                        ifDebug {
                            musicManager.defaultList.run {
                                Log.d("InitViewMode", "$name\n$list")
                            }
                            musicManager.likeList.run {
                                Log.d("InitViewMode", "$name\n$list")
                            }
                            musicManager.myLists.forEach {
                                it.run {
                                    Log.d("InitViewMode", "$name\n$list")
                                }
                            }
                        }
                    }, {
                        initRefreshState.value.error("初始化列表失败")?.let(initRefreshState::setValue)
                        ifDebug { println(it) }
                    }).also { disposables.add(it) }
        }
    }

    fun cancel() {
        disposables.clear()
        scanRefreshState.value = RefreshState.Error("扫描中断")
        initRefreshState.value = RefreshState.Empty
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}
