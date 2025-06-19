package com.ruparts.app.features.cart.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruparts.app.R
import com.ruparts.app.features.cart.model.CartListItem

class CartListAdapter: ListAdapter<CartListItem, CartListAdapter.CartItemViewHolder>(CartListItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_list_child, parent, false)
        return CartItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    class CartItemViewHolder(
        itemView: View
    ): RecyclerView.ViewHolder(itemView) {

        private val article = itemView.findViewById<TextView>(R.id.article)
        private val brand = itemView.findViewById<TextView>(R.id.brand)
        private val amount = itemView.findViewById<TextView>(R.id.amount)
        private val description = itemView.findViewById<TextView>(R.id.description)
        private val barcode = itemView.findViewById<TextView>(R.id.scanner)
        private val cartOwner = itemView.findViewById<TextView>(R.id.cart_owner)

        fun bind(listItem: CartListItem) {
            article.text = listItem.article
            brand.text = listItem.brand
            amount.text = listItem.amount.toString()
            description.text = listItem.description
            barcode.text = listItem.barcode
            cartOwner.text = listItem.cartOwner
        }


    }
}


class CartListItemDiffCallback : DiffUtil.ItemCallback<CartListItem>() {

    override fun areItemsTheSame(oldItem: CartListItem, newItem: CartListItem): Boolean {
        return oldItem.article == newItem.article
    }

    override fun areContentsTheSame(oldItem: CartListItem, newItem: CartListItem): Boolean {
        return oldItem == newItem
    }
}

