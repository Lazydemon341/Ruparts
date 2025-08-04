package com.ruparts.app.features.cart.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruparts.app.R
import com.ruparts.app.features.commonlibrary.ProductFlag
import com.ruparts.app.features.commonlibrary.presentation.getIconRes

class ProductFlagsIconsAdapter() : ListAdapter<ProductFlag, ProductFlagsIconsAdapter.FlagIconViewHolder>(FlagDiffCallback()) {

    init {
        setHasStableIds(true)
    }

    override fun submitList(list: List<ProductFlag>?) {
        val list = list?.filter { item ->
            item.getIconRes() != null
        }
        super.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlagIconViewHolder {
        val image = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_flag_icon, parent, false) as ImageView
        return FlagIconViewHolder(image)
    }

    override fun onBindViewHolder(holder: FlagIconViewHolder, position: Int) {
        val flag = getItem(position)
        holder.bind(flag)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }

    class FlagIconViewHolder(
        private val image: ImageView,
    ) : RecyclerView.ViewHolder(image) {

        fun bind(flag: ProductFlag) {
            val drawableRes = flag.getIconRes()
            if (drawableRes != null) {
                image.setImageResource(drawableRes)
                image.isVisible = true
            } else {
                image.isVisible = false
            }
        }
    }

    class FlagDiffCallback : DiffUtil.ItemCallback<ProductFlag>() {
        override fun areItemsTheSame(oldItem: ProductFlag, newItem: ProductFlag): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductFlag, newItem: ProductFlag): Boolean {
            return oldItem.id == newItem.id
        }
    }
}