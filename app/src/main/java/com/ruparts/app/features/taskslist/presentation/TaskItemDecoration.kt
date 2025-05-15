package com.ruparts.app.features.taskslist.presentation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ruparts.app.R

/**
 * Custom item decoration for the tasks RecyclerView
 * Adds dividers between group items for better visual separation
 */
class TaskItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    
    private val dividerPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.light_gray)
        style = Paint.Style.STROKE
        strokeWidth = context.resources.getDimension(R.dimen.divider_height)
    }
    
    private val dividerHeight = context.resources.getDimension(R.dimen.divider_height).toInt()
    private val groupSpacing = context.resources.getDimensionPixelSize(R.dimen.group_spacing)
    
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position < 0) return
        
        val adapter = parent.adapter as? ExpandableTaskAdapter ?: return

        if (position > 0 && adapter.getItemViewType(position) == ExpandableTaskAdapter.TYPE_GROUP) {
            outRect.top = groupSpacing
        }
    }
    
    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val adapter = parent.adapter as? ExpandableTaskAdapter ?: return
        
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)
            
            if (position < 0) continue

            if (position > 0 && adapter.getItemViewType(position) == ExpandableTaskAdapter.TYPE_GROUP) {
                val top = child.top - groupSpacing / 2 - dividerHeight / 2
                val bottom = top + dividerHeight
                
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
