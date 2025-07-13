package com.ruparts.app.features.cart.data.repository

import com.google.gson.Gson
import com.ruparts.app.core.data.network.EndpointRetrofitService
import com.ruparts.app.core.data.network.request
import com.ruparts.app.core.utils.runCoroutineCatching
import com.ruparts.app.features.cart.data.mapper.CartMapper
import com.ruparts.app.features.cart.data.network.model.CartRequestDto
import com.ruparts.app.features.cart.data.network.model.CartResponseDto
import com.ruparts.app.features.cart.data.network.model.CartTransferToBasketRequestDataDto
import com.ruparts.app.features.cart.data.network.model.CartTransferToBasketRequestDto
import com.ruparts.app.features.cart.data.network.model.CartTransferToBasketResponseDto
import com.ruparts.app.features.cart.data.network.model.CartTransferToBasketResponseDataDto
import com.ruparts.app.features.cart.model.CartListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CartRepository @Inject constructor(
    private val endpointService: EndpointRetrofitService,
    private val gson: Gson,
    private val mapper: CartMapper
) {
    suspend fun getCart(): Result<List<CartListItem>?> = withContext(Dispatchers.Default) {
        runCoroutineCatching {
            coroutineScope {
                val cartItems = async {
                    val response = endpointService.request<CartRequestDto, CartResponseDto>(
                        body = CartRequestDto(),
                        gson = gson,
                    )
                    mapper.mapCartItems(response.data?.items.orEmpty())
                }
                cartItems.await()
            }
        }
    }

    suspend fun doScan(): Result<CartTransferToBasketResponseDataDto> = withContext(Dispatchers.Default) {
        runCoroutineCatching {
            val response = endpointService.request<CartTransferToBasketRequestDto, CartTransferToBasketResponseDto>(
                body = CartTransferToBasketRequestDto(
                    data = CartTransferToBasketRequestDataDto(
                        barcode = TODO(),
                        bcTypes = TODO(),
                        purpose = TODO()
                    )
                ),
                gson = gson,
            )
            response.data
        }
    }

}
