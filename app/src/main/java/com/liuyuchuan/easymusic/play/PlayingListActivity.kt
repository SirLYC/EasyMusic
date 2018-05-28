package com.liuyuchuan.easymusic.play

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.liuyuchuan.easymusic.BaseActivity
import com.liuyuchuan.easymusic.R
import com.liuyuchuan.easymusic.data.Song
import com.liuyuchuan.easymusic.utils.*
import com.liuyuchuan.easymusic.widget.LinearItemDivider
import kotlinx.android.synthetic.main.activity_playing_list.*
import me.drakeet.multitype.MultiTypeAdapter

/**
 * Created by Liu Yuchuan on 2018/5/24.
 */
class PlayingListActivity : BaseActivity(), View.OnClickListener, CheckableItemViewBinder.OnRealItemClickListener<Song> {

    private lateinit var playingViewModel: PlayingListViewModel
    private lateinit var adapter: MultiTypeAdapter
    private lateinit var checkableItemViewBinder: PlaySongItemViewBinder

    private var menuItemEdit: MenuItem? = null
    private var menuItemDelete: MenuItem? = null
    private var menuItemCheckAll: MenuItem? = null
    private var menuItemFinish: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing_list)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.title_activity_playing_list)

        playingViewModel = provideViewModel()
        playingViewModel.init()

        fab_to_playing.setOnClickListener(this)

        adapter = ReactiveAdapter(playingViewModel.playList).apply {
            checkableItemViewBinder = PlaySongItemViewBinder(this@PlayingListActivity)
            register(songCheckableItem::class.java, checkableItemViewBinder)
            observe(this@PlayingListActivity)
        }

        rv_playing_list.adapter = adapter
        rv_playing_list.layoutManager = LinearLayoutManager(this)
        rv_playing_list.addItemDecoration(LinearItemDivider(this))
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab_to_playing -> PlayActivity.start(this)
        }
    }

    override fun onCheckRealItemClicked(realItem: Song) {
        playingViewModel.chooseSongToPlay(realItem)
        PlayActivity.start(this, true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_list_editable, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        menuItemEdit = menu.findItem(R.id.list_edit)
        menuItemDelete = menu.findItem(R.id.list_delete)
        menuItemCheckAll = menu.findItem(R.id.list_check_all)
        menuItemFinish = menu.findItem(R.id.list_complete)

        playingViewModel.enableCheckLiveData.observe(this, Observer {
            enableCheckAction(it!!)
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.list_edit -> {
                playingViewModel.enableCheck(true)
                true
            }

            R.id.list_complete -> {
                playingViewModel.enableCheck(false)
                true
            }

            R.id.list_check_all -> {
                if (checkableItemViewBinder.isCheckAll()) {
                    checkableItemViewBinder.uncheckAll()
                } else {
                    checkableItemViewBinder.checkAll()
                }
                true
            }

            R.id.list_delete -> {
                val list = checkableItemViewBinder.checkedItemList()
                if (list.isEmpty()) {
                    toast(R.string.error_no_songs_checked)
                } else {
                    playingViewModel.playList.removeAll(list)
                    playingViewModel.enableCheck(false)
                }
                true
            }

            else -> false
        }
    }

    private fun enableCheckAction(enable: Boolean) {
        if (enable) {
            menuItemEdit?.isVisible = false
            menuItemCheckAll?.isVisible = true
            menuItemDelete?.isVisible = true
            menuItemFinish?.isVisible = true
        } else {
            menuItemEdit?.isVisible = true
            menuItemCheckAll?.isVisible = false
            menuItemDelete?.isVisible = false
            menuItemFinish?.isVisible = false
        }

        checkableItemViewBinder.enableCheck = enable
    }

}
