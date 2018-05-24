package com.liuyuchuan.easymusic.history

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.liuyuchuan.easymusic.R
import com.liuyuchuan.easymusic.data.Song
import me.drakeet.multitype.ItemViewBinder

/**
 * Created by Liu Yuchuan on 2018/5/10.
 */
class HistoryItemViewBinder(
        private val onSongItemClickListener: OnSongItemClickListener
) : ItemViewBinder<Song, HistoryItemViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return inflater.inflate(R.layout.item_song, parent, false)
                .let { ViewHolder(it, onSongItemClickListener) }
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Song) {
        holder.bind(item)
    }

    class ViewHolder(itemView: View,
            private val onSongItemClickListener: OnSongItemClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val info: TextView = itemView.findViewById(R.id.tv_song_info)

        private var song: Song? = null

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(item: Song) {
            this.song = item
            info.text = item.run {
                "$singer-$name\n" +
                        "album:$album" +
                        "duration:$duration"
            }
        }

        override fun onClick(v: View) {
            when (v) {
                itemView -> song?.let(onSongItemClickListener::onSongItemClick)
            }
        }
    }

    interface OnSongItemClickListener {
        fun onSongItemClick(item: Song)
    }
}
