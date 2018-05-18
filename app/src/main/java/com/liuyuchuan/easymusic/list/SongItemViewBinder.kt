package com.liuyuchuan.easymusic.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.liuyuchuan.easymusic.R
import com.liuyuchuan.easymusic.data.Song
import com.liuyuchuan.easymusic.utils.CheckableItemViewBinder

/**
 * Created by Liu Yuchuan on 2018/5/10.
 */
class SongItemViewBinder(
        private val onSongItemClickListener: OnSongItemClickListener
) : CheckableItemViewBinder<SongItemViewBinder.ViewHolder, Song>() {

    override fun onCreateRealView(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return inflater.inflate(R.layout.item_song, parent, true)
                .let { ViewHolder(it, onSongItemClickListener) }
    }

    override fun onBindRealViewHolder(holder: ViewHolder, item: Song) {
        holder.bind(item, enableCheck)
    }

    class ViewHolder(itemView: View,
            private val onSongItemClickListener: OnSongItemClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val info: TextView = itemView.findViewById(R.id.tv_song_info)

        private var song: Song? = null
        private var enableCheck = false

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(item: Song, enableCheck: Boolean) {
            this.enableCheck = enableCheck
            this.song = item
            info.text = item.run {
                "$singer-$name\n" +
                        "album:$album" +
                        "duration:$duration"
            }
        }

        override fun onClick(v: View) {
            when (v) {
                itemView -> if (!enableCheck) {
                    song?.let(onSongItemClickListener::onSongItemClick)
                }
            }
        }
    }

    interface OnSongItemClickListener {
        fun onSongItemClick(item: Song)
    }
}
