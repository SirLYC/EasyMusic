package com.liuyuchuan.easymusic.history

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.liuyuchuan.easymusic.BaseActivity
import com.liuyuchuan.easymusic.R
import com.liuyuchuan.easymusic.data.Song
import com.liuyuchuan.easymusic.play.PlayActivity
import com.liuyuchuan.easymusic.utils.ReactiveAdapter
import com.liuyuchuan.easymusic.utils.RefreshState
import com.liuyuchuan.easymusic.utils.provideViewModel
import com.liuyuchuan.easymusic.utils.toast
import com.liuyuchuan.easymusic.widget.LinearItemDivider
import kotlinx.android.synthetic.main.activity_history.*
import me.drakeet.multitype.MultiTypeAdapter

/**
 * Created by Liu Yuchuan on 2018/5/24.
 */
class HistoryActivity : BaseActivity(), View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, HistoryItemViewBinder.OnSongItemClickListener {

    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var adapter: MultiTypeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.title_activity_history)

        historyViewModel = provideViewModel()

        srl_history.setOnRefreshListener(this)
        fab_to_playing.setOnClickListener(this)

        adapter = ReactiveAdapter(historyViewModel.historyList).apply {
            register(Song::class.java, HistoryItemViewBinder(this@HistoryActivity))
            observe(this@HistoryActivity)
        }

        rv_history.adapter = adapter
        rv_history.layoutManager = LinearLayoutManager(this)
        rv_history.addItemDecoration(LinearItemDivider(this))

        historyViewModel.refreshState.observe(this, Observer {
            when (it) {
                RefreshState.Empty -> {
                    historyViewModel.getHistoryList()
                    srl_history.isRefreshing = false
                }

                is RefreshState.Refreshing -> srl_history.isRefreshing = true

                else -> {
                    srl_history.isRefreshing = false
                }
            }
        })

        historyViewModel.refreshEvent.observe(this, Observer {
            if (it is RefreshState.Error) {
                toast(it.msg)
            }
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab_to_playing -> PlayActivity.start(this)
        }
    }

    override fun onSongItemClick(item: Song) {
        historyViewModel.chooseSongToPlay(item)
        PlayActivity.start(this, true)
    }

    override fun onRefresh() {
        historyViewModel.getHistoryList()
    }


}
