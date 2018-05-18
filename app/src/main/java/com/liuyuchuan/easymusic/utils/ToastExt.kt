package com.liuyuchuan.easymusic.utils

import android.app.Activity
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.widget.Toast

/**
 * Created by Liu Yuchuan on 2018/5/2.
 */
fun Activity.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(msg: String) {
    Toast.makeText(context!!, msg, Toast.LENGTH_SHORT).show()
}

fun Activity.toast(@StringRes msgRes: Int) {
    Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(@StringRes msgRes: Int) {
    Toast.makeText(context!!, msgRes, Toast.LENGTH_SHORT).show()
}

fun Activity.toastLong(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun Fragment.toastLong(msg: String) {
    Toast.makeText(context!!, msg, Toast.LENGTH_LONG).show()
}

fun Activity.toastLong(@StringRes msgRes: Int) {
    Toast.makeText(this, msgRes, Toast.LENGTH_LONG).show()
}

fun Fragment.toastLong(@StringRes msgRes: Int) {
    Toast.makeText(context!!, msgRes, Toast.LENGTH_LONG).show()
}
