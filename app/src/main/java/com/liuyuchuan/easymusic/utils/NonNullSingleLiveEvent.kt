package com.liuyuchuan.easymusic.utils

import android.support.annotation.NonNull

/**
 * Created by Liu Yuchuan on 2018/5/2.
 */
class NonNullSingleLiveEvent<T>(initValue: T) : SingleLiveEvent<T>() {

    init {
        value = initValue
    }

    override fun setValue(@NonNull value: T?) {
        super.setValue(value!!) // nonnull
    }

    override fun postValue(value: T) {
        super.postValue(value!!) // nonnull
    }

    override fun getValue(): T {
        return super.getValue()!! // nonnull
    }
}
