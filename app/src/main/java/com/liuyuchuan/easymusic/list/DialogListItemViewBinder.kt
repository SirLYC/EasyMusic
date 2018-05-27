package com.liuyuchuan.easymusic.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.liuyuchuan.easymusic.R
import com.liuyuchuan.easymusic.data.MusicList
import com.liuyuchuan.easymusic.utils.CheckableItem
import me.drakeet.multitype.ItemViewBinder

/**
 * Created by Liu Yuchuan on 2018/5/27.
 */
class DialogListItemViewBinder(
        private val onMusicListItemClickListener: OnMusicListItemClickListener
) : ItemViewBinder<CheckableItem<MusicList>, DialogListItemViewBinder.ViewHolder>() {


    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return inflater.inflate(R.layout.item_dialog_list_item, parent, false).let {
            ViewHolder(it, onMusicListItemClickListener)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, item: CheckableItem<MusicList>) {
        holder.bind(item.realItem)
    }

    class ViewHolder(
            v: View,
            private val onMusicListItemClickListener: OnMusicListItemClickListener
    ) : RecyclerView.ViewHolder(v) {
        private val name: TextView = v.findViewById(R.id.tv_dialog_add_to_list_list_name)

        private var musicList: MusicList? = null

        init {
            v.setOnClickListener { musicList?.let(onMusicListItemClickListener::onMusicListClick) }
        }

        fun bind(musicList: MusicList) {
            this.musicList = musicList
            name.text = musicList.name
        }
    }

    interface OnMusicListItemClickListener {
        fun onMusicListClick(musicList: MusicList)
    }
}
