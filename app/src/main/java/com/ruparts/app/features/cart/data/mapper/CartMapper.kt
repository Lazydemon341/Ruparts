package com.ruparts.app.features.cart.data.mapper

import com.ruparts.app.core.utils.toLocalDate
import com.ruparts.app.features.cart.data.network.model.CartItemDto
import com.ruparts.app.features.cart.model.CartListItem
import com.ruparts.app.features.taskslist.data.network.model.TaskDto
import com.ruparts.app.features.taskslist.data.network.model.toDomain
import com.ruparts.app.features.taskslist.model.TaskListGroup
import com.ruparts.app.features.taskslist.model.TaskListItem
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class CartMapper @Inject constructor() {

    fun mapCartItems(list: List<CartItemDto>): List<CartListItem> {
        return list.map { item ->
            mapCartItem(item)
        }
    }

    private fun mapCartItem(item: CartItemDto): CartListItem {
        return CartListItem(
            article = item.vendorCode,
            brand = item.brand,
            quantity = item.quantity,
            description = item.description,
            barcode = item.barcode,
            cartOwner = item.location
        )
    }
}