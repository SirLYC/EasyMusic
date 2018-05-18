package com.liuyuchuan.easymusic.utils

import android.app.DialogFragment

/**
 * Created by Liu Yuchuan on 2018/5/16.
 */
class RetryDialog : DialogFragment() {

    interface onRetryClickListener {
        fun onRetryClick()
        fun onCancelRetry()
    }
}
