package com.ruparts.app.features.search.data.repository

import com.google.gson.Gson
import com.ruparts.app.core.data.network.EndpointRetrofitService
import com.ruparts.app.core.data.network.request
import com.ruparts.app.core.utils.runCoroutineCatching
import com.ruparts.app.features.cart.data.mapper.CartMapper
import com.ruparts.app.features.cart.model.CartListItem
import com.ruparts.app.features.commonlibrary.data.repository.CommonLibraryRepository
import com.ruparts.app.features.search.data.mapper.SearchSetMapper
import com.ruparts.app.features.search.data.network.model.SearchListRequestDataDto
import com.ruparts.app.features.search.data.network.model.SearchListRequestDto
import com.ruparts.app.features.search.data.network.model.SearchListResponseDto
import com.ruparts.app.features.search.data.network.model.SearchSetsRequestDataDto
import com.ruparts.app.features.search.data.network.model.SearchSetsRequestDto
import com.ruparts.app.features.search.data.network.model.SearchSetsResponseDto
import com.ruparts.app.features.search.model.SearchSetItem
import com.ruparts.app.features.search.presentation.SearchScreenSorting
import com.ruparts.app.features.search.presentation.SearchScreenSortingType
import com.ruparts.app.features.search.presentation.SortingDirection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val endpointService: EndpointRetrofitService,
    private val gson: Gson,
    private val cartMapper: CartMapper,
    private val searchSetMapper: SearchSetMapper,
    private val commonLibraryRepository: CommonLibraryRepository,
) {

    suspend fun getList(
        locationFilter: String,
        flags: List<Long>,
        sets: List<Long>,
        search: String,
        sorting: SearchScreenSorting,
    ): Result<List<CartListItem>> = withContext(Dispatchers.Default) {
        runCoroutineCatching {
            supervisorScope {
                val searchListDeferred = async {
                    endpointService.request<SearchListRequestDto, SearchListResponseDto>(
                        body = SearchListRequestDto(
                            data = SearchListRequestDataDto(
                                filter = SearchListRequestDataDto.SearchFilter(
                                    location = locationFilter.takeIf { it.isNotEmpty() },
                                    flags = flags.takeIf { it.isNotEmpty() },
                                    sets = sets.takeIf { it.isNotEmpty() },
                                    search = search.takeIf { it.isNotEmpty() },
                                ),
                                sorting = SearchListRequestDataDto.Sorting(
                                    field = when (sorting.type) {
                                        SearchScreenSortingType.VENDOR_CODE -> "vendor_code"
                                        SearchScreenSortingType.QUANTITY -> "quantity"
                                        SearchScreenSortingType.BRAND -> "brand"
                                        SearchScreenSortingType.LOCATION -> "location"
                                    },
                                    direction = when (sorting.direction) {
                                        SortingDirection.ASCENDING -> "ASC"
                                        SortingDirection.DESCENDING -> "DESC"
                                    }
                                )
                            )
                        ),
                        gson = gson,
                    )
                }
                val productFlagsDeferred = async { commonLibraryRepository.getProductFlags() }

                val list = searchListDeferred.await()
                val productFlags = productFlagsDeferred.await()

                cartMapper.mapCartItems(
                    list = list.data.list,
                    flags = productFlags,
                )
            }
        }
    }

    suspend fun getSearchSets(): Result<List<SearchSetItem>> = withContext(Dispatchers.Default) {
        runCoroutineCatching {
            val searchSetResponse = endpointService.request<SearchSetsRequestDto, SearchSetsResponseDto>(
                body = SearchSetsRequestDto(
                    data = SearchSetsRequestDataDto()
                ),
                gson = gson,
            )

            searchSetMapper.mapSearchSets(searchSetResponse.data)
        }
    }
}
