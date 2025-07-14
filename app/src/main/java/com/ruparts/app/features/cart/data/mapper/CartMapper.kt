package com.ruparts.app.features.cart.data.mapper

import com.ruparts.app.features.cart.data.network.model.CartItemDto
import com.ruparts.app.features.cart.model.CartListItem
import javax.inject.Inject

class CartMapper @Inject constructor() {

    fun mapCartItems(list: List<CartItemDto>): List<CartListItem> {
        return list.map { item ->
            mapCartItem(item)
        }
    }

    fun mapCartItem(item: CartItemDto): CartListItem {
        return CartListItem(
            id = item.id,
            article = item.vendorCode,
            brand = item.brand,
            quantity = item.quantity,
            description = item.description,
            barcode = item.barcode,
            cartOwner = item.location
        )
    }
}