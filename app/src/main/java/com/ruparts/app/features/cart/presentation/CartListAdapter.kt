package com.ruparts.app.features.cart.presentation

import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruparts.app.R
import com.ruparts.app.features.cart.model.CartListItem

class CartListAdapter(
    private val onItemClick: (CartListItem) -> Unit,
): ListAdapter<CartListItem, CartListAdapter.CartItemViewHolder>(CartListItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_list_child, parent, false)
        return CartItemViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    class CartItemViewHolder(
        private val itemView: View,
        private val onItemClick: (model: CartListItem) -> Unit,
    ): RecyclerView.ViewHolder(itemView) {

        private val article = itemView.findViewById<TextView>(R.id.article)
        private val brand = itemView.findViewById<TextView>(R.id.brand)
        private val amount = itemView.findViewById<TextView>(R.id.amount)
        private val description = itemView.findViewById<TextView>(R.id.description)
        private val barcode = itemView.findViewById<TextView>(R.id.scanner)
        private val cartOwner = itemView.findViewById<TextView>(R.id.cart_owner)

        private val menu = itemView.findViewById<ImageButton>(R.id.menu_button)


        fun bind(listItem: CartListItem) {

            itemView.setOnClickListener { onItemClick(listItem) }

            article.text = listItem.article
            brand.text = listItem.brand
            amount.text = listItem.quantity.toString()
            description.text = listItem.description
            barcode.text = listItem.barcode
            cartOwner.text = listItem.cartOwner

            menu.setOnClickListener { view ->
                showPopupMenu(view)
            }
        }

        private fun showPopupMenu(view: View) {
            val context = itemView.context
            val popupMenu = PopupMenu(context, view)
            MenuInflater(context).inflate(R.menu.cart_item_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when(item.itemId){
                    R.id.split -> {
                        Toast.makeText(context, "Выбрали первый пункт меню", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.reprint -> {
                        Toast.makeText(context, "Выбрали второй пункт меню", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.defect -> {
                        Toast.makeText(context, "Выбрали третий пункт меню", Toast.LENGTH_SHORT).show()
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()
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

