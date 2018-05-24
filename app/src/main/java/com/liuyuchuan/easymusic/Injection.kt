package com.liuyuchuan.easymusic

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.liuyuchuan.easymusic.data.SongRepository
import com.liuyuchuan.easymusic.db.DBHelper
import com.liuyuchuan.easymusic.history.HistoryViewModel
import com.liuyuchuan.easymusic.list.ListManageViewModel
import com.liuyuchuan.easymusic.list.SongListViewModel
import com.liuyuchuan.easymusic.utils.MusicManager

/**
 * Created by Liu Yuchuan on 2018/5/2.
 */
class Injection(app: Application) : ViewModelProvider.AndroidViewModelFactory(app) {
    private val musicManager = MusicManager(app)
    private val songRepository = SongRepository(DBHelper(app))

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.run {
            when {
                isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel(musicManager, songRepository)
                isAssignableFrom(ListManageViewModel::class.java) -> ListManageViewModel(musicManager, songRepository)
                isAssignableFrom(SongListViewModel::class.java) -> SongListViewModel(musicManager)
                isAssignableFrom(HistoryViewModel::class.java) -> HistoryViewModel(songRepository, musicManager)
                else -> super.create(modelClass)
            }
        } as T
    }

    fun musicManager() = musicManager
    fun songRepository() = songRepository
}
