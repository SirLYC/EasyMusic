package com.liuyuchuan.easymusic

import android.Manifest
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import com.liuyuchuan.easymusic.list.ListManageActivity
import com.liuyuchuan.easymusic.utils.RefreshState
import com.liuyuchuan.easymusic.utils.WaitingDialog
import com.liuyuchuan.easymusic.utils.provideViewModel
import com.liuyuchuan.easymusic.utils.toast
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_init.*

/**
 * Created by Liu Yuchuan on 2018/5/6.
 */
class InitActivity : BaseActivity(), View.OnClickListener, WaitingDialog.OnWaitingCanceledListener {
    private lateinit var initViewModel: InitViewModel
    private lateinit var rxPermissions: RxPermissions
    private var waitingDialog: WaitingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)
        rxPermissions = RxPermissions(this)
        initViewModel = provideViewModel()

        bt_enter_without_update.setOnClickListener(this)
        bt_update_local_list.setOnClickListener(this)

        initViewModel.scanRefreshEvent.observe(this, Observer {
            when (it) {
                is RefreshState.Error -> toast(it.msg)
                is RefreshState.NotEmpty -> {
                    toast(R.string.hint_scan_finished)
                    initViewModel.init(false)
                }
            }
        })

        initViewModel.scanRefreshState.observe(this, Observer {
            when (it) {
                is RefreshState.Refreshing -> {
                    waitingDialog = WaitingDialog.show(supportFragmentManager, getString(R.string.content_scanning), this)
                    bt_update_local_list.isEnabled = false
                    bt_enter_without_update.isEnabled = false
                }

                else -> {
                    bt_enter_without_update.isEnabled = true
                    bt_update_local_list.isEnabled = true
                    waitingDialog?.dismissAllowingStateLoss()
                }
            }
        })

        initViewModel.initRefreshEvent.observe(this, Observer {
            when (it) {
                is RefreshState.Error -> toast(it.msg)
                is RefreshState.NotEmpty -> ListManageActivity.start(this)
            }
        })

        initViewModel.initRefreshState.observe(this, Observer {
            when (it) {
                is RefreshState.Refreshing -> {
                    waitingDialog = WaitingDialog.show(supportFragmentManager, getString(R.string.content_reading_list), this)
                    bt_update_local_list.isEnabled = false
                    bt_enter_without_update.isEnabled = false
                }

                else -> {
                    bt_enter_without_update.isEnabled = true
                    bt_update_local_list.isEnabled = true
                    waitingDialog?.dismissAllowingStateLoss()
                }
            }
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bt_enter_without_update -> {
                rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe { granted ->
                            if (granted) {
                                initViewModel.init(true)
                            } else {
                                toast(R.string.error_no_permission)
                            }
                        }
            }
            R.id.bt_update_local_list -> {
                rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe { granted ->
                            if (granted) {
                                initViewModel.scan()
                            } else {
                                toast(R.string.error_no_permission)
                            }
                        }
            }
        }
    }

    override fun onCancelWaiting() {
        initViewModel.cancel()
    }
}
