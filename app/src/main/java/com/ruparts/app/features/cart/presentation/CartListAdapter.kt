package com.ruparts.app.features.cart.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruparts.app.R
import com.ruparts.app.features.cart.model.CartListItem
import com.ruparts.app.features.cart.presentation.cancelbutton.CartItemCancelButton
import kotlinx.coroutines.flow.StateFlow


class CartListAdapter(
    private val onItemClick: (CartListItem) -> Unit,
    private val onCancelClick: (CartListItem) -> Unit,
    private val cancelButtonLoaderState: StateFlow<Float>,
) : ListAdapter<CartListItem, CartListAdapter.CartItemViewHolder>(CartListItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_list_child, parent, false)
        return CartItemViewHolder(view, onItemClick, onCancelClick, cancelButtonLoaderState)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }

    class CartItemViewHolder(
        private val itemView: View,
        private val onItemClick: (model: CartListItem) -> Unit,
        private val onCancelClick: (CartListItem) -> Unit,
        private val cancelButtonLoaderState: StateFlow<Float>,
    ) : RecyclerView.ViewHolder(itemView) {

        private val article = itemView.findViewById<TextView>(R.id.article)
        private val brand = itemView.findViewById<TextView>(R.id.brand)
        private val amount = itemView.findViewById<TextView>(R.id.amount)
        private val description = itemView.findViewById<TextView>(R.id.description)
        private val barcode = itemView.findViewById<TextView>(R.id.scanner)
        private val cartOwner = itemView.findViewById<TextView>(R.id.cart_owner)
        private val cancelButton = itemView.findViewById<CartItemCancelButton>(R.id.cart_cancel_button)

        private val menu = itemView.findViewById<ImageButton>(R.id.menu_button)


        fun bind(listItem: CartListItem) {
            itemView.setOnClickListener { onItemClick(listItem) }
            cancelButton.setOnClickListener { onCancelClick(listItem) }

            article.text = listItem.article
            brand.text = listItem.brand
            amount.text = listItem.quantity.toString()
            description.text = listItem.description
            barcode.text = listItem.barcode
            cartOwner.text = listItem.cartOwner

            menu.setOnClickListener { view ->
                showPopupMenu(view)
            }

            if (listItem.fromExternalInput) {
                cancelButton.setLoaderState(cancelButtonLoaderState)
                cancelButton.isVisible = true
            } else {
                cancelButton.setLoaderState(null)
                cancelButton.isVisible = false
            }
        }

        @SuppressLint("RestrictedApi")
        private fun showPopupMenu(view: View) {
            val context = view.context

            val popupMenu = PopupMenu(context, view)
            MenuInflater(context).inflate(R.menu.cart_item_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
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

            val menuHelper = MenuPopupHelper(context, popupMenu.menu as MenuBuilder, view)
            menuHelper.setForceShowIcon(true)
            menuHelper.show()
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

