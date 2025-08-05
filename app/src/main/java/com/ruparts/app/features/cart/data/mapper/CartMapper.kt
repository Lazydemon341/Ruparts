package com.ruparts.app.features.cart.data.mapper

import com.ruparts.app.features.cart.data.network.model.CartItemDto
import com.ruparts.app.features.cart.model.CartListItem
import com.ruparts.app.features.commonlibrary.ProductFlag
import javax.inject.Inject

class CartMapper @Inject constructor() {

    fun mapCartItems(
        list: List<CartItemDto>,
        flags: Map<Long, ProductFlag> = emptyMap(),
    ): List<CartListItem> {
        return list.map { item ->
            mapCartItem(item, flags)
        }
    }

    fun mapCartItem(
        item: CartItemDto,
        flags: Map<Long, ProductFlag> = emptyMap(),
    ): CartListItem {
        return CartListItem(
            id = item.id,
            article = item.vendorCode,
            brand = item.brand,
            quantity = item.quantity,
            description = item.description,
            barcode = item.barcode,
            cartOwner = item.location,
            flags = item.flags?.mapNotNull { flags[it] }.orEmpty(),
            info = item.info.orEmpty(),
        )
    }
}