package com.liuyuchuan.easymusic

import android.Manifest
import android.arch.lifecycle.Observer
import android.os.Bundle
import com.liuyuchuan.easymusic.list.ListManageActivity
import com.liuyuchuan.easymusic.utils.RefreshState
import com.liuyuchuan.easymusic.utils.provideViewModel
import com.liuyuchuan.easymusic.utils.toast
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_init.*

/**
 * Created by Liu Yuchuan on 2018/5/6.
 */
class SplashActivity : BaseActivity() {
    private lateinit var splashViewModel: SplashViewModel
    private lateinit var rxPermissions: RxPermissions

    private val r = Runnable {
        ListManageActivity.start(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)
        rxPermissions = RxPermissions(this)
        splashViewModel = provideViewModel()

        val start = System.currentTimeMillis()
        splashViewModel.scanRefreshState.observe(this, Observer {
            when (it) {
                is RefreshState.Empty -> rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe { granted ->
                            if (granted) {
                                splashViewModel.scan()
                            } else {
                                toast(R.string.error_no_permission)
                                finish()
                            }
                        }
                is RefreshState.NotEmpty -> {
                    val du = System.currentTimeMillis() - start
                    if (du > 1500) {
                        r.run()
                    } else {
                        tv_welcome.postDelayed(r, 1500 - du)
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        tv_welcome.removeCallbacks(r)
        super.onDestroy()
    }
}
