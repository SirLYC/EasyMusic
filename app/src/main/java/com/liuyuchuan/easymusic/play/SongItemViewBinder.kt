package com.liuyuchuan.easymusic.play

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
class PlaySongItemViewBinder(
        onRealItemClickListener: OnRealItemClickListener<Song>
) : CheckableItemViewBinder<Song, PlaySongItemViewBinder.ViewHolder>(onRealItemClickListener) {

    override fun onCreateRealView(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return inflater.inflate(R.layout.item_song, parent, true)
                .let { ViewHolder(it) }
    }

    override fun onBindRealViewHolder(holder: ViewHolder, item: Song) {
        holder.bind(item)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val info: TextView = itemView.findViewById(R.id.tv_song_info)

        private var song: Song? = null

        fun bind(item: Song) {
            this.song = item
            info.text = item.run {
                "$singer-$name\n" +
                        "album:$album" +
                        "duration:$duration"
            }
        }
    }
}
