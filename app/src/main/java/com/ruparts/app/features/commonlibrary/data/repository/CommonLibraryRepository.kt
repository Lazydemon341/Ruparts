package com.ruparts.app.features.commonlibrary.data.repository

import com.google.gson.Gson
import com.ruparts.app.core.data.network.EndpointRetrofitService
import com.ruparts.app.core.data.network.request
import com.ruparts.app.core.utils.runCoroutineCatching
import com.ruparts.app.features.commonlibrary.CommonLibraryData
import com.ruparts.app.features.commonlibrary.ProductFlag
import com.ruparts.app.features.commonlibrary.data.mapper.CommonLibraryMapper
import com.ruparts.app.features.commonlibrary.data.network.model.GetLibraryRequestDto
import com.ruparts.app.features.commonlibrary.data.network.model.GetLibraryResponseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommonLibraryRepository @Inject constructor(
    private val endpointService: EndpointRetrofitService,
    private val gson: Gson,
    private val mapper: CommonLibraryMapper
) {

    private val libraryDispatcher = Dispatchers.Default.limitedParallelism(1)

    private var libraryData: CommonLibraryData? = null

    suspend fun getProductFlags(): Map<Long, ProductFlag> {
        return getLibrary()?.productFlags.orEmpty()
    }

    private suspend fun getLibrary(): CommonLibraryData? = runCoroutineCatching {
        libraryData?.let { return it }

        return withContext(libraryDispatcher) {
            libraryData?.let { return@withContext it }

            val response = endpointService.request<GetLibraryRequestDto, GetLibraryResponseDto>(
                body = GetLibraryRequestDto(),
                gson = gson,
            )

            return@withContext mapper.mapCommonLibrary(response.data)?.also {
                libraryData = it
            }
        }
    }.getOrNull()
}
