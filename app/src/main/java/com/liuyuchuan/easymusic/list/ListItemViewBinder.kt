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
        onRealItemClickListener: OnRealItemClickListener<MusicList>
) : CheckableItemViewBinder<MusicList, ListItemViewBinder.ViewHolder>(onRealItemClickListener) {
    override fun onCreateRealView(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return inflater.inflate(R.layout.item_list, parent, true)
                .let { ViewHolder(it) }
    }

    override fun onBindRealViewHolder(holder: ViewHolder, item: MusicList) {
        holder.bind(item)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var musicList: MusicList? = null
        private val info: TextView = itemView.findViewById(R.id.tv_info_item_list)


        fun bind(item: MusicList) {
            this.musicList = item
            info.text = ("${item.name}\n" +
                    "song count: ${item.list.size}\n")
        }
    }
}
