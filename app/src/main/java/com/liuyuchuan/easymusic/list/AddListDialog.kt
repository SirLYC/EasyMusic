package com.liuyuchuan.easymusic.list

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.liuyuchuan.easymusic.R
import com.liuyuchuan.easymusic.utils.RefreshState
import com.liuyuchuan.easymusic.utils.provideViewModel
import com.liuyuchuan.easymusic.utils.toast

/**
 * Created by Liu Yuchuan on 2018/5/24.
 */
class AddListDialog : DialogFragment(), TextWatcher, View.OnClickListener {

    private lateinit var textInputLayout: TextInputLayout
    private lateinit var textInputEditText: TextInputEditText
    private lateinit var okButton: Button
    private lateinit var cancelButton: Button
    private lateinit var listManageViewModel: ListManageViewModel

    companion object {
        private const val TAG_ADD_LIST_DIALOG = "TAG_ADD_LIST_DIALOG"

        fun show(fm: FragmentManager) {
            val f = fm.findFragmentByTag(TAG_ADD_LIST_DIALOG)
            if (f != null) {
                fm.beginTransaction()
                        .remove(f)
                        .commitAllowingStateLoss()
            }

            fm.beginTransaction()
                    .add(AddListDialog(), TAG_ADD_LIST_DIALOG)
                    .commitAllowingStateLoss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val ctx = context!!
        val v = LayoutInflater.from(ctx).inflate(R.layout.layout_dialog_add_list, null)
        textInputLayout = v.findViewById(R.id.titl_add_list)
        textInputEditText = v.findViewById(R.id.tiet_add_list)
        okButton = v.findViewById(R.id.bt_add_list_ok)
        cancelButton = v.findViewById(R.id.bt_add_list_cancel)

        textInputEditText.addTextChangedListener(this)
        okButton.setOnClickListener(this)
        cancelButton.setOnClickListener(this)

        listManageViewModel = activity!!.provideViewModel()

        listManageViewModel.addListRefreshEvent.observe(this, Observer {
            when (it) {
                is RefreshState.Error -> textInputLayout.error = it.msg
                is RefreshState.NotEmpty -> {
                    toast("添加成功!")
                    dismissAllowingStateLoss()
                }
            }
        })

        listManageViewModel.addListRefreshState.observe(this, Observer {
            when (it) {
                is RefreshState.Refreshing -> okButton.isEnabled = false
                is RefreshState.NotEmpty -> {
                    okButton.isEnabled = false
                    dismissAllowingStateLoss()
                }
                else -> okButton.isEnabled = true
            }
        })
        return AlertDialog.Builder(ctx)
                .setMessage("添加音乐列表")
                .setOnCancelListener {
                    listManageViewModel.addListRefreshState.value = RefreshState.Empty
                }
                .setView(v)
                .create()
    }


    override fun afterTextChanged(s: Editable?) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        textInputLayout.isErrorEnabled = false
    }

    private fun attemptToAddList() {
        val name = textInputEditText.text.trim().toString()
        if (name.isEmpty()) {
            textInputLayout.error = getString(R.string.error_empty_list_name)
            return
        }

        listManageViewModel.addSongList(name)
    }

    override fun onClick(v: View) {
        when (v) {
            okButton -> attemptToAddList()
            cancelButton -> dismissAllowingStateLoss()
        }
    }
}
