package com.liuyuchuan.easymusic.utils

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.support.annotation.MainThread
import android.util.Log
import com.liuyuchuan.easymusic.ifDebug
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by Liu Yuchuan on 2018/5/2.
 */
open class SingleLiveEvent<T> : MutableLiveData<T>() {

    private val pending = AtomicBoolean(false)

    @MainThread
    override fun setValue(value: T?) {
        pending.set(true)
        super.setValue(value)
    }

    override fun postValue(value: T) {
        pending.set(true)
        super.postValue(value)
    }

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        ifDebug {
            if (hasActiveObservers()) {
                Log.w("SingleLiveEvent", "observe SingleLiveEvent more than once")
            }
        }

        super.observe(owner, Observer {
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(it)
            }
        })
    }

    @MainThread
    override fun observeForever(observer: Observer<T>) {
        ifDebug {
            if (hasActiveObservers()) {
                Log.w("SingleLiveEvent", "observe SingleLiveEvent more than once")
            }
        }

        super.observeForever {
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(it)
            }
        }
    }
}
