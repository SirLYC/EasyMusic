package com.liuyuchuan.easymusic.play

import android.arch.lifecycle.Observer
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import com.liuyuchuan.easymusic.BaseActivity
import com.liuyuchuan.easymusic.R
import com.liuyuchuan.easymusic.data.Song
import com.liuyuchuan.easymusic.history.HistoryActivity
import com.liuyuchuan.easymusic.utils.PlayState
import com.liuyuchuan.easymusic.utils.formatTime
import com.liuyuchuan.easymusic.utils.toast
import kotlinx.android.synthetic.main.activity_play.*
import kotlin.math.roundToInt

/**
 * Created by Liu Yuchuan on 2018/5/22.
 */
class PlayActivity : BaseActivity(), View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private lateinit var musicConnection: MusicConnection
    private lateinit var musicBinder: MusicService.MusicBinder

    private val TOTAL_PROGRESS = 1000

    companion object {
        private const val NAME_NEW_MUSIC = "NAME_NEW_MUSIC"

        fun start(context: Context, playNewMusic: Boolean = false) {
            context.startActivity(Intent(context, PlayActivity::class.java).apply {
                putExtra(NAME_NEW_MUSIC, playNewMusic)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        enableAction(false)
        acsb_play_progress.setOnSeekBarChangeListener(this)
        acsb_play_progress.max = 1000
        iv_play_or_pause.setOnClickListener(this)
        iv_play_next.setOnClickListener(this)
        iv_play_pre.setOnClickListener(this)
        iv_play_list.setOnClickListener(this)
        iv_play_mode.setOnClickListener(this)

        val intent = Intent(this, MusicService::class.java)
        startService(intent)
        musicConnection = MusicConnection()
        bindService(intent, musicConnection, Context.BIND_ADJUST_WITH_ACTIVITY)
    }

    private fun enableAction(enable: Boolean) {
        iv_play_or_pause.isEnabled = enable
        iv_play_next.isEnabled = enable
        iv_play_pre.isEnabled = enable
        acsb_play_progress.isEnabled = enable
    }

    private fun setSongInfoAction(playingSong: Song?) {
        if (playingSong != null) {
            tv_play_song_info.text = playingSong.run {
                tv_play_position_now.setText(R.string.hint_time_0)
                tv_play_duration.text = duration.formatTime()
                "$singer-$name\n" +
                        "album:$album\n" +
                        "path:$path"
            }
        } else {
            Log.d("PlayActivity", "openPlayActivityWithNoSong")
            tv_play_song_info.setText(R.string.hint_no_song_playing)
            tv_play_position_now.setText(R.string.hint_time_0)
            tv_play_duration.setText(R.string.hint_time_0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_play, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.history -> {
                startActivity(Intent(this, HistoryActivity::class.java))
                true
            }
            else -> false
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val start = intent?.getBooleanExtra(NAME_NEW_MUSIC, false) ?: false
        if (start) {
            intent?.putExtra(NAME_NEW_MUSIC, false)
        }
        musicBinder.prepare(start)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_play_next -> musicBinder.playNext()
            R.id.iv_play_pre -> musicBinder.playPre()
            R.id.iv_play_or_pause -> musicBinder.playOrPause()
            R.id.iv_play_mode -> musicBinder.changeMode()
            R.id.iv_play_list -> startActivity(Intent(this, PlayingListActivity::class.java))
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            musicBinder.playPositionAt(progress / (TOTAL_PROGRESS.toFloat()))
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    inner class MusicConnection : ServiceConnection {
        override fun onBindingDied(name: ComponentName) {}

        override fun onServiceDisconnected(name: ComponentName) {}

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            musicBinder = service as MusicService.MusicBinder
            val start = intent.getBooleanExtra(NAME_NEW_MUSIC, false)
            if (start) {
                intent.putExtra(NAME_NEW_MUSIC, false)
            }
            musicBinder.prepare(start)

            musicBinder.playModeLiveData().observe(this@PlayActivity, Observer {
                it?.let {
                    when (it) {
                        MusicService.PLAY_ORDER -> iv_play_mode.setImageResource(R.drawable.ic_list)
                        MusicService.PLAY_ALL_REPEAT -> iv_play_mode.setImageResource(R.drawable.ic_repeat)
                        MusicService.PLAY_SINGLE_REPEAT -> iv_play_mode.setImageResource(R.drawable.ic_repeat_one)
                        MusicService.PLAY_SHUFFLE -> iv_play_mode.setImageResource(R.drawable.ic_shuffle)
                        else -> {
                            Log.w("PlayActivity", "unknown play mode($it)")
                            iv_play_mode.setImageResource(R.drawable.ic_list)
                        }
                    }
                }
            })

            musicBinder.playingSongLiveData().observe(this@PlayActivity, Observer { setSongInfoAction(it) })
            // NonNullLiveData
            musicBinder.playProgressLiveData().observe(this@PlayActivity, Observer {
                val progress = it!!
                if (progress.total > 0) {
                    tv_play_position_now.text = progress.current.formatTime()
                    tv_play_duration.text = progress.total.formatTime()
                    acsb_play_progress.progress = ((progress.current / progress.total.toFloat()) * TOTAL_PROGRESS).roundToInt()
                } else {
                    acsb_play_progress.progress = 0
                    tv_play_position_now.setText(R.string.hint_time_0)
                    tv_play_duration.setText(R.string.hint_time_0)
                }
            })

            // play state callback
            musicBinder.playState().observe(this@PlayActivity, Observer {
                when (it) {
                    is PlayState.NotReady, PlayState.Preparing -> {
                        enableAction(false)
                    }
                    is PlayState.ResourceReady -> {
                        enableAction(true)
                        iv_play_or_pause.setImageResource(R.drawable.ic_play)
                    }
                    is PlayState.Playing -> {
                        enableAction(true)
                        iv_play_or_pause.setImageResource(R.drawable.ic_pause)
                    }
                }
            })
            musicBinder.playEvent().observe(this@PlayActivity, Observer {
                if (it is PlayState.PlayError) {
                    toast(it.msg)
                }
            })
        }
    }

    override fun onDestroy() {
        unbindService(musicConnection)
        super.onDestroy()
    }
}
