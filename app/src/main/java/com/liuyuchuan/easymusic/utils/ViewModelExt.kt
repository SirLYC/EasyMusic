package com.liuyuchuan.easymusic.utils

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.liuyuchuan.easymusic.App

/**
 * Created by Liu Yuchuan on 2018/5/2.
 */
inline fun <reified T : ViewModel> FragmentActivity.provideViewModel(): T {
    return ViewModelProviders.of(this, App.INSTANCE.injector()).get(T::class.java)
}

inline fun <reified T : ViewModel> Fragment.provideViewModel(): T {
    return ViewModelProviders.of(this, App.INSTANCE.injector()).get(T::class.java)
}
