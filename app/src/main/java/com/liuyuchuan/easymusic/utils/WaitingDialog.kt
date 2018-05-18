package com.liuyuchuan.easymusic.utils

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.widget.ProgressBar
import com.liuyuchuan.easymusic.R

/**
 * Created by Liu Yuchuan on 2018/5/16.
 */
class WaitingDialog : DialogFragment(), DialogInterface.OnClickListener {
    override fun onClick(dialog: DialogInterface?, which: Int) {
        if (which == DialogInterface.BUTTON_NEGATIVE) {
            onWaitingCanceledListener?.onCancelWaiting()
        }
    }

    var onWaitingCanceledListener: OnWaitingCanceledListener? = null

    companion object {
        private const val NAME_TITLE = "NAME_TITLE"
        private const val TAG = "WaitingDialog"

        fun show(fragmentManager: FragmentManager, title: String, onWaitingCanceledListener: OnWaitingCanceledListener? = null): WaitingDialog {
            return WaitingDialog().apply {
                this.onWaitingCanceledListener = onWaitingCanceledListener
                arguments = Bundle().apply {
                    putString(NAME_TITLE, title)
                }

                val oldDialog = fragmentManager.findFragmentByTag(TAG)
                if (oldDialog != null) {
                    fragmentManager.beginTransaction()
                            .remove(oldDialog)
                            .commitAllowingStateLoss()
                }

                fragmentManager.beginTransaction()
                        .add(this, "Waiting")
                        .commitAllowingStateLoss()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context!!
        val title = arguments?.getString(NAME_TITLE) ?: "请等待"
        return AlertDialog.Builder(context)
                .setNegativeButton(R.string.action_cancel, this)
                .setCancelable(false)
                .setTitle(title)
                .setView(ProgressBar(context).apply {
                    val density = context.resources.displayMetrics.density
                    val dp8 = (8 * density + 0.5).toInt()
                    setPadding(dp8, dp8, dp8, dp8)
                })
                .create()
    }

    interface OnWaitingCanceledListener {
        fun onCancelWaiting()
    }
}
