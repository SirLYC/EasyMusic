package com.liuyuchuan.easymusic.utils

/**
 * Created by Liu Yuchuan on 2018/5/2.
 */
sealed class PlayState {
    open fun play(): PlayState? = null
    open fun pause(): PlayState? = null
    open fun prepare(): PlayState? = null
    open fun prepareResult(ready: Boolean): PlayState? = null
    fun reset(): PlayState = NotReady
    fun error(msg: String): PlayState = PlayError(msg)

    object NotReady : PlayState() {
        override fun prepare() = Preparing
    }

    // pause or stop
    object ResourceReady : PlayState() {
        override fun play() = Playing
    }

    object Playing : PlayState() {
        override fun pause() = ResourceReady
    }

    class PlayError(
            val msg: String
    ) : PlayState() {
        override fun prepare() = Preparing
    }

    object Preparing : PlayState() {
        override fun prepareResult(ready: Boolean): PlayState? {
            return if (ready) {
                ResourceReady
            } else {
                NotReady
            }
        }
    }
}
