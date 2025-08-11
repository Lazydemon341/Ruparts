package com.ruparts.app.features.product.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruparts.app.R
import com.ruparts.app.core.utils.capitalize
import com.ruparts.app.features.commonlibrary.ProductFlag
import com.ruparts.app.features.commonlibrary.presentation.getIconRes

class ProductFlagsAdapter : ListAdapter<ProductFlag, ProductFlagsAdapter.FlagViewHolder>(FlagDiffCallback()) {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlagViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_flag, parent, false) as TextView
        return FlagViewHolder(textView)
    }

    override fun onBindViewHolder(holder: FlagViewHolder, position: Int) {
        val flag = getItem(position)
        holder.bind(flag)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }

    class FlagViewHolder(private val textView: TextView) : RecyclerView.ViewHolder(textView) {
        fun bind(flag: ProductFlag) {
            textView.text = flag.title.capitalize()
            val iconRes = flag.getIconRes() ?: 0
            if (iconRes != 0) {
                val drawable = ContextCompat.getDrawable(textView.context, iconRes)?.mutate()
                drawable?.let {
                    DrawableCompat.setTint(it, textView.context.getColor(R.color.onSurfaceVariant))
                    textView.setCompoundDrawablesRelativeWithIntrinsicBounds(it, null, null, null)
                }
            } else {
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            }
        }
    }

    class FlagDiffCallback : DiffUtil.ItemCallback<ProductFlag>() {
        override fun areItemsTheSame(oldItem: ProductFlag, newItem: ProductFlag): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductFlag, newItem: ProductFlag): Boolean {
            return oldItem == newItem
        }
    }
}
