package com.liuyuchuan.easymusic

import android.support.v4.app.Fragment

/**
 * Created by Liu Yuchuan on 2018/5/7.
 */
abstract class BaseFragment : Fragment() {

    // return handled by self
    open fun onBackPressed(): Boolean {
        return false
    }
}
