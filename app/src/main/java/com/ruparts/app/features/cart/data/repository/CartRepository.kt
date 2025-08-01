package com.ruparts.app.features.cart.data.repository

import com.google.gson.Gson
import com.ruparts.app.core.data.network.EndpointRetrofitService
import com.ruparts.app.core.data.network.request
import com.ruparts.app.core.utils.runCoroutineCatching
import com.ruparts.app.features.cart.data.mapper.CartMapper
import com.ruparts.app.features.cart.data.network.model.CartRequestDto
import com.ruparts.app.features.cart.data.network.model.CartResponseDto
import com.ruparts.app.features.cart.data.network.model.CartScanBCTypeDto
import com.ruparts.app.features.cart.data.network.model.CartScanRequestDataDto
import com.ruparts.app.features.cart.data.network.model.CartScanRequestDto
import com.ruparts.app.features.cart.data.network.model.CartScanRequestPurposeDto
import com.ruparts.app.features.cart.data.network.model.CartScanResponseDto
import com.ruparts.app.features.cart.data.network.model.CartTransferToBasketRequestDataDto
import com.ruparts.app.features.cart.data.network.model.CartTransferToBasketRequestDto
import com.ruparts.app.features.cart.data.network.model.CartTransferToLocationRequestDataDto
import com.ruparts.app.features.cart.data.network.model.CartTransferToLocationRequestDto
import com.ruparts.app.features.cart.model.CartListItem
import com.ruparts.app.features.cart.model.CartScanPurpose
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val SUCCESS_RESPONSE_TYPE = 0
private const val ERROR_RESPONSE_TYPE = 1

class CartRepository @Inject constructor(
    private val endpointService: EndpointRetrofitService,
    private val gson: Gson,
    private val mapper: CartMapper
) {

    suspend fun getCart(): Result<List<CartListItem>> = withContext(Dispatchers.Default) {
        runCoroutineCatching {
            val response = endpointService.request<CartRequestDto, CartResponseDto>(
                body = CartRequestDto(),
                gson = gson,
            )
            mapper.mapCartItems(response.data?.items.orEmpty())
        }
    }

    suspend fun scanProduct(
        barcode: String,
        purpose: CartScanPurpose = CartScanPurpose.TRANSFER_TO_CART,
    ): Result<CartListItem> = withContext(Dispatchers.Default) {
        runCoroutineCatching {
            val response = endpointService.request<CartScanRequestDto, CartScanResponseDto>(
                body = CartScanRequestDto(
                    data = CartScanRequestDataDto(
                        barcode = barcode,
                        bcTypes = listOf(CartScanBCTypeDto.PRODUCT),
                        purpose = when (purpose) {
                            CartScanPurpose.TRANSFER_TO_LOCATION -> CartScanRequestPurposeDto.TRANSFER_TO_LOCATION
                            CartScanPurpose.TRANSFER_TO_CART -> CartScanRequestPurposeDto.TRANSFER_TO_BASKET
                            CartScanPurpose.INFO -> CartScanRequestPurposeDto.INFO
                        },
                    )
                ),
                gson = gson,
            )

            when (response.type) {
                SUCCESS_RESPONSE_TYPE -> {
                    val data = requireNotNull(
                        value = response.data,
                        lazyMessage = { "Success response with null data" },
                    )
                    mapper.mapCartItem(data.scannedItem)
                }

                ERROR_RESPONSE_TYPE -> {
                    throw CartScanException(response.error?.message)
                }

                else -> throw IllegalStateException("Unknown response type: ${response.type}")
            }
        }
    }

    suspend fun scanLocation(
        barcode: String,
    ): Result<Unit> = withContext(Dispatchers.Default) {
        runCoroutineCatching {
            val response = endpointService.request<CartScanRequestDto, CartScanResponseDto>(
                body = CartScanRequestDto(
                    data = CartScanRequestDataDto(
                        barcode = barcode,
                        bcTypes = listOf(CartScanBCTypeDto.LOCATION_PLACE, CartScanBCTypeDto.LOCATION_CELL),
                        purpose = CartScanRequestPurposeDto.TRANSFER_TO_LOCATION,
                    )
                ),
                gson = gson,
            )
        }
    }

    suspend fun transferToCart(barcodes: List<String>): Result<Unit> = withContext(Dispatchers.Default) {
        runCoroutineCatching {
            val response = endpointService.request<CartTransferToBasketRequestDto, CartResponseDto>(
                body = CartTransferToBasketRequestDto(
                    data = CartTransferToBasketRequestDataDto(
                        barcodes = barcodes
                    )
                ),
                gson = gson
            )
            Unit
        }
    }

    suspend fun transferToLocation(barcodes: List<String>, location: String): Result<Unit> = withContext(Dispatchers.Default) {
        runCoroutineCatching {
            endpointService.request<CartTransferToLocationRequestDto, CartResponseDto>(
                body = CartTransferToLocationRequestDto(
                    data = CartTransferToLocationRequestDataDto(
                        barcodes = barcodes,
                        location = location,
                    )
                ),
                gson = gson
            )
            Unit
        }
    }

}

class CartScanException(message: String?) : Exception(message)

