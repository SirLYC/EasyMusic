package com.liuyuchuan.easymusic.list

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.liuyuchuan.easymusic.BaseActivity
import com.liuyuchuan.easymusic.R
import com.liuyuchuan.easymusic.data.MusicList
import com.liuyuchuan.easymusic.play.PlayActivity
import com.liuyuchuan.easymusic.utils.provideViewModel
import kotlinx.android.synthetic.main.activity_list_manage.*

/**
 * Created by Liu Yuchuan on 2018/5/6.
 */
class ListManageActivity : BaseActivity(), View.OnClickListener {
    private lateinit var listManageViewModel: ListManageViewModel

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ListManageActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_manage)
        setSupportActionBar(toolbar)

        fab_to_playing.setOnClickListener(this)

        listManageViewModel = provideViewModel()

        listManageViewModel.selectedMusicListLiveData.observe(this, Observer(this::switchFragment))
    }

    private fun switchFragment(musicList: MusicList?) {
        if (musicList == null) {
            val tag = SongListListFragment::class.java.name
            if (supportFragmentManager.findFragmentByTag(tag) == null) {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.container_list_manage, SongListListFragment(), tag)
                        .commitAllowingStateLoss()
            }
        } else {
            val tag = SongListFragment::class.java.name
            if (supportFragmentManager.findFragmentByTag(tag) == null) {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.container_list_manage, SongListFragment(), tag)
                        .addToBackStack(tag)
                        .commitAllowingStateLoss()
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            listManageViewModel.selectedMusicListLiveData.value = null
        } else {
            super.onBackPressed()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab_to_playing -> PlayActivity.start(this)
        }
    }
}
