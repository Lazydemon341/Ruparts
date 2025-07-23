package com.ruparts.app.features.cart.presentation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ruparts.app.R

class CartItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val dividerPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.surfaceContainer)
        style = Paint.Style.STROKE
        strokeWidth = context.resources.getDimension(R.dimen.cart_item_spacing)
    }

    private val dividerHeight = context.resources.getDimension(R.dimen.cart_item_spacing).toInt()
    private val itemSpacing = context.resources.getDimensionPixelSize(R.dimen.cart_item_spacing)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position < 0) return

        val adapter = parent.adapter as? CartListAdapter ?: return

        outRect.top = itemSpacing
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val adapter = parent.adapter as? CartListAdapter ?: return

        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)

            if (position < 0) continue

            if (position > 0) {
                val top = child.top - dividerHeight / 2
                val bottom = top

                canvas.drawLine(
                    left.toFloat(),
                    top.toFloat(),
                    right.toFloat(),
                    bottom.toFloat(),
                    dividerPaint
                )
            }
        }
    }
}