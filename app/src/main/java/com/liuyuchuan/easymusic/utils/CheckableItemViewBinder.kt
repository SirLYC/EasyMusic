package com.liuyuchuan.easymusic.utils

import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import com.liuyuchuan.easymusic.R
import kotlinx.android.synthetic.main.item_checkable.view.*
import me.drakeet.multitype.ItemViewBinder

/**
 * Created by Liu Yuchuan on 2018/5/9.
 */
abstract class CheckableItemViewBinder<T, VH : RecyclerView.ViewHolder>(
        private val onRealItemClickListener: OnRealItemClickListener<T>
) : ItemViewBinder<CheckableItem<T>, CheckableItemViewBinder.ViewHolder<T, VH>>() {

    var enableCheck = false
        set(value) {
            val old = field
            field = value
            if (value xor old) {
                adapter.items.forEachIndexed { index, item ->
                    if (item is CheckableItem<*>) {
                        adapter.notifyItemChanged(index)
                    }
                }
            }
        }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder<T, VH> {
        return inflater.inflate(R.layout.item_checkable, parent, false).let {
            ViewHolder(it, onCreateRealView(inflater, it.container_real_item), onRealItemClickListener)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder<T, VH>, item: CheckableItem<T>) {
        holder.bind(item, enableCheck)
        onBindRealViewHolder(holder.realHolder, item.realItem)
    }

    abstract fun onCreateRealView(inflater: LayoutInflater, parent: ViewGroup): VH

    abstract fun onBindRealViewHolder(holder: VH, item: T)

    class ViewHolder<in T, out VH>(view: View,
            val realHolder: VH,
            private val onRealItemClickListener: OnRealItemClickListener<T>) : RecyclerView.ViewHolder(view),
            CompoundButton.OnCheckedChangeListener, View.OnClickListener {

        private val checkBox: AppCompatCheckBox = itemView.findViewById(R.id.accb_selectable_item)
        private var checkableItem: CheckableItem<T>? = null
        private var enableCheck = false

        init {
            checkBox.setOnCheckedChangeListener(this)
            itemView.setOnClickListener(this)
        }

        fun bind(checkableItem: CheckableItem<T>, enableCheck: Boolean) {
            this.checkableItem = checkableItem
            this.enableCheck = enableCheck
            if (checkableItem.checkable && enableCheck) {
                checkBox.visibility = View.VISIBLE
                checkBox.isChecked = checkableItem.isChecked
            } else {
                checkBox.visibility = View.GONE
                checkBox.isChecked = false
                checkableItem.isChecked = false
            }
        }

        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            checkableItem?.isChecked = isChecked
        }

        override fun onClick(v: View) {
            when (v) {
                itemView -> if (enableCheck) {
                    checkBox.performClick()
                } else {
                    checkableItem?.realItem?.let(onRealItemClickListener::onCheckRealItemClicked)
                }
            }
        }
    }

    fun isCheckAll(): Boolean {
        if (!enableCheck) {
            return false
        }

        adapter.items.forEach {
            if (it is CheckableItem<*> && !it.isChecked) {
                return false
            }
        }

        return true
    }

    fun checkAll() {
        if (!enableCheck) {
            return
        }
        adapter.items.forEachIndexed { index, item ->
            if (item is CheckableItem<*> && item.checkable) {
                item.isChecked = true
                adapter.notifyItemChanged(index)
            }
        }
    }

    fun uncheckAll() {
        if (!enableCheck) {
            return
        }
        adapter.items.forEachIndexed { index, item ->
            if (item is CheckableItem<*> && item.checkable) {
                item.isChecked = false
                adapter.notifyItemChanged(index)
            }
        }
    }

    fun checkedItemList(): List<CheckableItem<T>> {
        val list = mutableListOf<CheckableItem<T>>()
        if (!enableCheck) {
            return list
        }

        adapter.items.forEach { item ->
            if (item is CheckableItem<*> && item.isChecked) {
                @Suppress("UNCHECKED_CAST")
                list.add(item as CheckableItem<T>)
            }
        }

        return list
    }

    interface OnRealItemClickListener<in T> {
        fun onCheckRealItemClicked(realItem: T)
    }
}
