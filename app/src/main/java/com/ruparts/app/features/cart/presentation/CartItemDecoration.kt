package com.ruparts.app.features.cart.presentation

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ruparts.app.R

class CartItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val itemSpacing = context.resources.getDimensionPixelSize(R.dimen.cart_list_item_spacing)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = itemSpacing
    }
}