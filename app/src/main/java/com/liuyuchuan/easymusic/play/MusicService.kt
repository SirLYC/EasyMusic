package com.liuyuchuan.easymusic.play

import android.app.Service
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.liuyuchuan.easymusic.App
import com.liuyuchuan.easymusic.data.Song
import com.liuyuchuan.easymusic.ifDebug
import com.liuyuchuan.easymusic.utils.*
import com.liuyuchuan.easymusic.utils.Optional
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.disposables.ListCompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

/**
 * Created by Liu Yuchuan on 2018/5/17.
 */
class MusicService : Service(), MediaPlayer.OnCompletionListener {

    private val disposables = ListCompositeDisposable()

    private val mediaPlayer = MediaPlayer()
            .also {
                it.setOnCompletionListener(this)
            }

    private val musicManager: MusicManager = App.INSTANCE.injector().musicManager()
    private val songRepository = App.INSTANCE.injector().songRepository()

    private val playingList = musicManager.playingList

    private var playingSong: Song? = null

    private val playState = NonNullLiveData<PlayState>(PlayState.NotReady)
    private val playEvent = NonNullSingleLiveEvent<PlayState>(PlayState.NotReady)
    private val playingSongLiveData = MutableLiveData<Song>()
    private val playModeLiveData = NonNullLiveData(PLAY_ALL_REPEAT)
    private val playProgressLiveData = NonNullLiveData(PlayProgress(0, 0))

    companion object {
        const val PLAY_ORDER = 0
        const val PLAY_ALL_REPEAT = 1
        const val PLAY_SINGLE_REPEAT = 2
        const val PLAY_SHUFFLE = 3
    }

    override fun onCreate() {
        super.onCreate()
        playState.observeForever { playEvent.value = it!! }
        playingSongLiveData.value = null
    }

    override fun onBind(intent: Intent?): IBinder {
        return MusicBinder()
    }

    private fun startCountProgress() {
        disposables.clear()
        Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    playProgressLiveData.value = PlayProgress(mediaPlayer.currentPosition, mediaPlayer.duration)
                }
                .also {
                    disposables.add(it)
                }
    }

    private fun recordHistory(song: Song) {
        songRepository.addHistory(song)
                .async()
                .subscribe({
                    // may use the result
                    if (musicManager.historyList.contains(song)) {
                        musicManager.historyList.remove(song)
                    }
                    musicManager.historyList.add(0, song)
                }, {
                    ifDebug {
                        println(it)
                    }
                })
    }

    override fun onCompletion(mp: MediaPlayer) {
        playState.value.pause()?.let(playState::setValue)
        disposables.clear()
        when (playModeLiveData.value) {
            PLAY_ORDER -> {
                musicManager.playPosition++
                if (musicManager.playPosition >= playingList.size) {
                    musicManager.playPosition = -1
                }
            }

            PLAY_ALL_REPEAT -> {
                if (++musicManager.playPosition >= playingList.size) {
                    musicManager.playPosition = 0
                }
            }

            PLAY_SHUFFLE -> {
                musicManager.playPosition = Random().nextInt(playingList.size)
            }

            else -> {
                Log.w("MusicService", "Unknown play mode( ${playModeLiveData.value}), use single repeat mode instead")
                musicManager.playPosition++
                if (musicManager.playPosition >= playingList.size) {
                    musicManager.playPosition = -1
                }
            }
        }

        play()
    }

    private fun play() {
        mediaPlayer.reset()
        playState.value.reset().let(playState::setValue)
        playState.value.prepare()?.let {
            playState.value = it
            doPlay()
        }
    }

    private fun doPlay() {
        Observable.create<Optional<Song>>({
            if (musicManager.playPosition < 0 || musicManager.playPosition >= playingList.size) {
                Log.d("MusicService", "cannot play, cuz playPosition = ${musicManager.playPosition}")
                it.onNext(Optional.ofNullable(null))
                return@create
            }

            val song = playingList[musicManager.playPosition]
            mediaPlayer.setDataSource(song.path)
            mediaPlayer.prepare()
            Log.d("MusicService", "play $song")
            it.onNext(Optional.of(song))
        })
                .async()
                .subscribe({
                    it.getNullable().let { song ->
                        if (song == null) {
                            playState.value.prepareResult(false)?.let(playState::setValue)
                        } else {
                            playState.value.prepareResult(true)?.let(playState::setValue)
                            playState.value.play()?.let {
                                mediaPlayer.start()
                                playState.value = it
                                startCountProgress()
                                recordHistory(song)
                            }
                        }
                        playingSongLiveData.value = song
                    }
                }, {
                    ifDebug {
                        it.printStackTrace()
                    }
                    if (it is IOException) {
                        playState.value.error("该歌曲不存在或已被删除")
                    } else {
                        playState.value.error("未知错误${it.message}")
                    }
                })
    }

    // offer playing commands and state query
    inner class MusicBinder : Binder() {

        fun start() {
            play()
        }

        fun playOrPause() {

            when (playState.value) {
                PlayState.Playing -> playState.value.pause()?.let {
                    mediaPlayer.pause()
                    playState.value = it
                }

                PlayState.ResourceReady -> playState.value.play()?.let {
                    mediaPlayer.start()
                    playState.value = it
                }
            }
        }

        // play next by button
        fun playNext() {
            mediaPlayer.stop()
            disposables.clear()

            when (playModeLiveData.value) {

                PLAY_SHUFFLE -> {
                    musicManager.playPosition = Random().nextInt(playingList.size)
                }

                else -> {
                    if (++musicManager.playPosition >= playingList.size) {
                        musicManager.playPosition = 0
                    }
                }
            }

            play()
        }

        // play pre by button
        fun playPre() {
            mediaPlayer.stop()
            disposables.clear()

            when (playModeLiveData.value) {

                PLAY_SHUFFLE -> {
                    musicManager.playPosition = Random().nextInt(playingList.size)
                }

                else -> {
                    if (--musicManager.playPosition < 0) {
                        musicManager.playPosition = playingList.size - 1
                    }
                }
            }

            play()
        }

        fun playPositionAt(progress: Float) {
            val duration = mediaPlayer.duration
            if (duration > 0) {
                mediaPlayer.seekTo((progress * duration).roundToInt())
            }
        }

        fun changeMode() {
            val oldMode = playModeLiveData.value
            playModeLiveData.value = (oldMode + 1) % 4
        }

        fun playModeLiveData() = playModeLiveData
        fun playingSongLiveData() = playingSongLiveData
        fun playProgressLiveData() = playProgressLiveData
        fun playState() = playState
        fun playEvent() = playEvent
    }

    class PlayProgress(
            val current: Int,
            val total: Int
    )

    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }
}
