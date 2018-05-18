package com.liuyuchuan.easymusic

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.liuyuchuan.easymusic.list.ListManageViewModel
import com.liuyuchuan.easymusic.utils.MusicManager

/**
 * Created by Liu Yuchuan on 2018/5/2.
 */
class Injection(app: Application) : ViewModelProvider.AndroidViewModelFactory(app) {
    val musicManager = MusicManager(app)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.run {
            when {
                isAssignableFrom(InitViewModel::class.java) -> InitViewModel(musicManager)
                isAssignableFrom(ListManageViewModel::class.java) -> ListManageViewModel(musicManager)
                else -> super.create(modelClass)
            }
        } as T
    }
}
