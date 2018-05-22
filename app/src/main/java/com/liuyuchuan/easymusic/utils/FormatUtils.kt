package com.liuyuchuan.easymusic.utils

import kotlin.math.roundToInt

/**
 * Created by Liu Yuchuan on 2018/5/22.
 */
fun Int.formatTime(): String {
    val totalSec = div(1000F).roundToInt()
    val sec = totalSec % 60
    val min = totalSec / 60
    return String.format("%d:%02d", min, sec)
}
