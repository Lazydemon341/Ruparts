package com.ruparts.app.features.cart.data.network.model

import com.ruparts.app.core.data.network.EndpointRequestDto
import com.ruparts.app.features.cart.model.CartListItem

class CartRequestDto : EndpointRequestDto<List<CartListItem>>(
    action = "mobile.product.common.basket_items",
    data = null,
)
