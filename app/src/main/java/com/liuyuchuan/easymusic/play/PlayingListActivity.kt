package com.liuyuchuan.easymusic.play

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.liuyuchuan.easymusic.BaseActivity
import com.liuyuchuan.easymusic.R
import com.liuyuchuan.easymusic.data.Song
import com.liuyuchuan.easymusic.utils.ReactiveAdapter
import com.liuyuchuan.easymusic.utils.provideViewModel
import kotlinx.android.synthetic.main.activity_playing_list.*
import me.drakeet.multitype.MultiTypeAdapter

/**
 * Created by Liu Yuchuan on 2018/5/24.
 */
class PlayingListActivity : BaseActivity(), View.OnClickListener, PlaySongItemViewBinder.OnSongItemClickListener {

    private lateinit var playingViewModel: PlayingListViewModel
    private lateinit var adapter: MultiTypeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing_list)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.title_activity_playing_list)

        playingViewModel = provideViewModel()

        fab_to_playing.setOnClickListener(this)

        adapter = ReactiveAdapter(playingViewModel.playingList()).apply {
            register(Song::class.java, PlaySongItemViewBinder(this@PlayingListActivity))
            observe(this@PlayingListActivity)
        }

        rv_playing_list.adapter = adapter
        rv_playing_list.layoutManager = LinearLayoutManager(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab_to_playing -> PlayActivity.start(this)
        }
    }

    override fun onSongItemClick(item: Song) {
        playingViewModel.chooseSongToPlay(item)
        PlayActivity.start(this, true)
    }
}
