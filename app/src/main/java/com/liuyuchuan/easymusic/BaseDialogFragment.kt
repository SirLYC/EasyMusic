package com.liuyuchuan.easymusic

import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager

/**
 * Created by Liu Yuchuan on 2018/5/6.
 */
abstract class BaseDialogFragment : DialogFragment() {
    override fun show(manager: FragmentManager, tag: String?) {
        val ft = manager.beginTransaction()
        ft.add(this, tag)
        ft.commit()
    }
}
