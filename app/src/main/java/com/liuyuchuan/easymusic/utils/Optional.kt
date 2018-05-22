package com.liuyuchuan.easymusic.utils

/**
 * Created by Liu Yuchuan on 2018/5/22.
 */
class Optional<out T> private constructor(private val value: T?) {

    companion object {
        private val EMPTY = Optional(null)

        @Suppress("UNCHECKED_CAST")
        fun <T> empty(): Optional<T> = EMPTY

        fun <T> of(value: T) = Optional(value)

        fun <T> ofNullable(value: T?) = Optional(value)
    }

    fun isPresent(): Boolean = value != null

    fun get(): T = value ?: throw NoSuchElementException("null")
    fun getNullable(): T? = value
}
