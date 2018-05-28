package com.liuyuchuan.easymusic.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.liuyuchuan.easymusic.R

/**
 * Created by Liu Yuchuan on 2018/5/28.
 */
class LinearItemDivider(
        context: Context,
        private val orientation: Int = LinearLayoutManager.VERTICAL,
        private val dividerWidth: Int = 1
) : RecyclerView.ItemDecoration() {

    private val divider: Drawable = ColorDrawable(ContextCompat.getColor(context, R.color.bg_default))

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        if (parent.adapter.itemCount - 1 == parent.getChildAdapterPosition(view)) {
            outRect.set(0, 0, 0, 0)
            return
        }

        if (orientation == LinearLayoutManager.VERTICAL) {
            outRect.set(0, 0, 0, dividerWidth)
        } else {
            outRect.set(0, 0, dividerWidth, 0)
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        when (orientation) {
            LinearLayoutManager.VERTICAL -> drawVertical(c, parent)
            LinearLayoutManager.HORIZONTAL -> drawHorizontal(c, parent)
        }
    }

    private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val top = parent.paddingTop
        val bottom = parent.measuredHeight - parent.paddingBottom
        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams
            val left = child.right + layoutParams.rightMargin
            val right = left + dividerWidth
            divider.setBounds(left, top, right, bottom)
            divider.draw(c)
        }
    }

    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.measuredWidth - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + layoutParams.bottomMargin
            val bottom = top + dividerWidth
            divider.setBounds(left, top, right, bottom)
            divider.draw(c)
        }
    }
}
