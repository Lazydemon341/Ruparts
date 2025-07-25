package com.ruparts.app.features.product.data.repository

import com.google.gson.Gson
import com.ruparts.app.core.data.network.EndpointRetrofitService
import com.ruparts.app.core.data.network.request
import com.ruparts.app.core.utils.runCoroutineCatching
import com.ruparts.app.features.product.data.mapper.ProductMapper
import com.ruparts.app.features.product.data.network.model.ReadProductRequestDataDto
import com.ruparts.app.features.product.data.network.model.ReadProductRequestDto
import com.ruparts.app.features.product.data.network.model.ReadProductResponseDto
import com.ruparts.app.features.product.domain.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val endpointService: EndpointRetrofitService,
    private val gson: Gson,
    private val mapper: ProductMapper,
) {
    suspend fun readProduct(barcode: String): Result<Product> = withContext(Dispatchers.Default) {
        runCoroutineCatching {
            val response = endpointService.request<ReadProductRequestDto, ReadProductResponseDto>(
                body = ReadProductRequestDto(ReadProductRequestDataDto(barcode)),
                gson = gson,
            )
            val dto = requireNotNull(response.data) { "No product data in response" }
            mapper.mapProduct(dto)
        }
    }
} 