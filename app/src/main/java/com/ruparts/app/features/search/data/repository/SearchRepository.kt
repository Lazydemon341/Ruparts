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
import com.ruparts.app.features.search.presentation.SearchScreenSearchSet
import com.ruparts.app.features.search.presentation.SearchScreenSorting
import com.ruparts.app.features.search.presentation.SearchScreenSortingType
import com.ruparts.app.features.search.presentation.SortingDirection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.Result
import kotlin.collections.orEmpty

class SearchRepository @Inject constructor(
    private val endpointService: EndpointRetrofitService,
    private val gson: Gson,
    private val cartMapper: CartMapper,
    private val searchSetMapper: SearchSetMapper,
    private val commonLibraryRepository: CommonLibraryRepository,
) {

    suspend fun getList(
        flags: List<Long>,
        sets: List<Long>,
        location: String,
        sorting: SearchScreenSorting
    ): Result<List<CartListItem>> = withContext(Dispatchers.Default) {
        runCoroutineCatching {
            supervisorScope {
                val searchListDeferred = async {
                    endpointService.request<SearchListRequestDto, SearchListResponseDto>(
                        body = SearchListRequestDto(
                            data = SearchListRequestDataDto(
                                filter = SearchListRequestDataDto.SearchFilter(
                                    flags = flags.takeIf { it.isNotEmpty() },
                                    sets = sets.takeIf { it.isNotEmpty() },
                                    location = location.takeIf { it.isNotEmpty() },
                                ),
                                sorting = SearchListRequestDataDto.Sorting(
                                    field = when (sorting.type) {
                                        SearchScreenSortingType.QUANTITY -> "quantity"
                                        SearchScreenSortingType.CELL_NUMBER -> TODO() //?
                                        SearchScreenSortingType.PURCHASE_PRICE -> TODO()
                                        SearchScreenSortingType.SELLING_PRICE -> TODO()
                                        SearchScreenSortingType.ARRIVAL_DATE -> TODO()
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
                    list = list.data?.list.orEmpty(),
                    flags = productFlags,
                )
            }
        }
    }

    suspend fun getSearchSets(): Result<List<SearchScreenSearchSet>> = withContext(Dispatchers.Default) {
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

