package com.liuyuchuan.easymusic.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.liuyuchuan.easymusic.R
import com.liuyuchuan.easymusic.data.MusicList
import com.liuyuchuan.easymusic.utils.CheckableItemViewBinder

/**
 * Created by Liu Yuchuan on 2018/5/9.
 */
class ListItemViewBinder(
        private val onListItemClickListener: OnListItemClickListener
) : CheckableItemViewBinder<ListItemViewBinder.ViewHolder, MusicList>() {
    override fun onCreateRealView(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return inflater.inflate(R.layout.item_list, parent, true)
                .let { ViewHolder(it, onListItemClickListener) }
    }

    override fun onBindRealViewHolder(holder: ViewHolder, item: MusicList) {
        holder.bind(item, enableCheck)
    }

    class ViewHolder(itemView: View,
            private val onListItemClickListener: OnListItemClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        override fun onClick(v: View) {
            when (v) {
                itemView -> if (!enableCheck) {
                    musicList?.let(onListItemClickListener::onSongListItemClick)
                }
            }
        }

        private var enableCheck = false
        private var musicList: MusicList? = null
        private val info: TextView = itemView.findViewById(R.id.tv_info_item_list)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(item: MusicList, enableCheck: Boolean) {
            this.musicList = item
            this.enableCheck = enableCheck
            info.text = ("${item.name}\n" +
                    "song count: ${item.list.size}\n")
        }
    }

    interface OnListItemClickListener {
        fun onSongListItemClick(item: MusicList)
    }

}
