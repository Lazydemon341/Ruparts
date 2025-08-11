package com.ruparts.app.features.search.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.gson.Gson
import com.ruparts.app.core.data.network.EndpointRetrofitService
import com.ruparts.app.core.data.network.request
import com.ruparts.app.core.utils.runCoroutineCatching
import com.ruparts.app.features.cart.model.CartListItem
import com.ruparts.app.features.search.data.mapper.SearchSetMapper
import com.ruparts.app.features.search.data.network.model.SearchSetsRequestDataDto
import com.ruparts.app.features.search.data.network.model.SearchSetsRequestDto
import com.ruparts.app.features.search.data.network.model.SearchSetsResponseDto
import com.ruparts.app.features.search.data.paging.SearchPagingSource
import com.ruparts.app.features.search.model.SearchSetItem
import com.ruparts.app.features.search.presentation.SearchScreenSorting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val endpointService: EndpointRetrofitService,
    private val gson: Gson,
    private val searchSetMapper: SearchSetMapper,
    private val searchPagingSourceFactory: SearchPagingSource.Factory,
) {

    fun getListPaged(
        locationFilter: String,
        flags: List<Long>,
        sets: List<Long>,
        search: String,
        sorting: SearchScreenSorting,
    ): Flow<PagingData<CartListItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                initialLoadSize = 70,
                enablePlaceholders = false,
                prefetchDistance = 50,
            ),
            pagingSourceFactory = {
                searchPagingSourceFactory.create(
                    locationFilter = locationFilter,
                    flags = flags,
                    sets = sets,
                    search = search,
                    sorting = sorting
                )
            }
        ).flow
    }

    suspend fun getSearchSets(search: String): Result<List<SearchSetItem>> = withContext(Dispatchers.Default) {
        runCoroutineCatching {
            val searchSetResponse = endpointService.request<SearchSetsRequestDto, SearchSetsResponseDto>(
                body = SearchSetsRequestDto(
                    data = SearchSetsRequestDataDto(
                        search = search.takeIf { it.isNotEmpty() },
                    )
                ),
                gson = gson,
            )
            searchSetMapper.mapSearchSets(searchSetResponse.data)
        }
    }
}
