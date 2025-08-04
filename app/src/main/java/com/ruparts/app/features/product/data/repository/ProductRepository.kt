package com.ruparts.app.features.product.data.repository

import com.google.gson.Gson
import com.ruparts.app.core.data.network.EndpointRetrofitService
import com.ruparts.app.core.data.network.request
import com.ruparts.app.core.utils.runCoroutineCatching
import com.ruparts.app.features.commonlibrary.data.repository.CommonLibraryRepository
import com.ruparts.app.features.product.data.mapper.ProductMapper
import com.ruparts.app.features.product.data.network.model.ReadProductRequestDataDto
import com.ruparts.app.features.product.data.network.model.ReadProductRequestDto
import com.ruparts.app.features.product.data.network.model.ReadProductResponseDto
import com.ruparts.app.features.product.domain.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val endpointService: EndpointRetrofitService,
    private val gson: Gson,
    private val mapper: ProductMapper,
    private val commonLibraryRepository: CommonLibraryRepository,
) {
    suspend fun readProduct(barcode: String): Result<Product> = withContext(Dispatchers.Default) {
        runCoroutineCatching {
            val productDto = async {
                val response = endpointService.request<ReadProductRequestDto, ReadProductResponseDto>(
                    body = ReadProductRequestDto(ReadProductRequestDataDto(barcode)),
                    gson = gson,
                )
                requireNotNull(response.data) { "No product data in response" }
            }
            val productFlags = async { commonLibraryRepository.getProductFlags() }

            mapper.mapProduct(productDto.await(), productFlags.await())
        }
    }
} 