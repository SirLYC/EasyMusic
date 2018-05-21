package com.liuyuchuan.easymusic

import android.Manifest
import android.arch.lifecycle.Observer
import android.os.Bundle
import com.liuyuchuan.easymusic.list.ListManageActivity
import com.liuyuchuan.easymusic.utils.RefreshState
import com.liuyuchuan.easymusic.utils.provideViewModel
import com.liuyuchuan.easymusic.utils.toast
import com.tbruyelle.rxpermissions2.RxPermissions

/**
 * Created by Liu Yuchuan on 2018/5/6.
 */
class InitActivity : BaseActivity() {
    private lateinit var initViewModel: InitViewModel
    private lateinit var rxPermissions: RxPermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)
        rxPermissions = RxPermissions(this)
        initViewModel = provideViewModel()

        initViewModel.scanRefreshState.observe(this, Observer {
            when (it) {
                is RefreshState.Empty -> rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe { granted ->
                            if (granted) {
                                initViewModel.scan()
                            } else {
                                toast(R.string.error_no_permission)
                                finish()
                            }
                        }
                is RefreshState.NotEmpty -> ListManageActivity.start(this)
            }
        })
    }
}
