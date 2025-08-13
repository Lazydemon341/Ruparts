package com.ruparts.app.features.search.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.ruparts.app.core.data.network.EndpointRetrofitService
import com.ruparts.app.core.data.network.request
import com.ruparts.app.features.cart.data.mapper.CartMapper
import com.ruparts.app.features.cart.model.CartListItem
import com.ruparts.app.features.commonlibrary.data.repository.CommonLibraryRepository
import com.ruparts.app.features.search.data.network.model.SearchListRequestDataDto
import com.ruparts.app.features.search.data.network.model.SearchListRequestDto
import com.ruparts.app.features.search.data.network.model.SearchListResponseDto
import com.ruparts.app.features.search.presentation.model.SearchScreenSorting
import com.ruparts.app.features.search.presentation.model.SearchScreenSortingType
import com.ruparts.app.features.search.presentation.model.SortingDirection
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException

class SearchPagingSource @AssistedInject constructor(
    private val endpointService: EndpointRetrofitService,
    private val gson: Gson,
    private val cartMapper: CartMapper,
    private val commonLibraryRepository: CommonLibraryRepository,
    @Assisted("locationFilter") private val locationFilter: String,
    @Assisted("flags") private val flags: List<Long>,
    @Assisted("sets") private val sets: List<Long>,
    @Assisted("search") private val search: String,
    @Assisted("sorting") private val sorting: SearchScreenSorting,
) : PagingSource<Int, CartListItem>() {

    private val seenIds = mutableSetOf<Long>()

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CartListItem> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize

            val response = endpointService.request<SearchListRequestDto, SearchListResponseDto>(
                body = buildRequestBody(page, pageSize),
                gson = gson,
            )

            val productFlags = commonLibraryRepository.getProductFlags()
            val allCartItems = cartMapper.mapCartItems(
                list = response.data.list,
                flags = productFlags,
            )

            // Filter out items with duplicate IDs
            val uniqueCartItems = allCartItems.filter { item ->
                if (item.id !in seenIds) {
                    seenIds.add(item.id)
                    true
                } else {
                    false
                }
            }

            val pagination = response.data.pagination
            val nextPage = if (page < (pagination.maxPage ?: 1)) page + 1 else null
            val prevPage = if (page > 1) page - 1 else null

            LoadResult.Page(
                data = uniqueCartItems,
                prevKey = prevPage,
                nextKey = nextPage
            )
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CartListItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private fun buildRequestBody(page: Int, pageSize: Int): SearchListRequestDto {
        return SearchListRequestDto(
            data = SearchListRequestDataDto(
                filter = SearchListRequestDataDto.SearchFilter(
                    location = locationFilter.takeIf { it.isNotEmpty() },
                    flags = flags.takeIf { it.isNotEmpty() },
                    sets = sets.takeIf { it.isNotEmpty() },
                    search = search.takeIf { it.isNotEmpty() },
                ),
                pagination = SearchListRequestDataDto.Pagination(
                    page = page,
                    perPage = pageSize
                ),
                sorting = SearchListRequestDataDto.Sorting(
                    field = when (sorting.type) {
                        SearchScreenSortingType.QUANTITY -> "quantity"
                        SearchScreenSortingType.VENDOR_CODE -> "vendor_code"
                        SearchScreenSortingType.BRAND -> "brand"
                        SearchScreenSortingType.LOCATION -> "location"
                    },
                    direction = when (sorting.direction) {
                        SortingDirection.ASCENDING -> "ASC"
                        SortingDirection.DESCENDING -> "DESC"
                    }
                )
            )
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("locationFilter") locationFilter: String,
            @Assisted("flags") flags: List<Long>,
            @Assisted("sets") sets: List<Long>,
            @Assisted("search") search: String,
            @Assisted("sorting") sorting: SearchScreenSorting,
        ): SearchPagingSource
    }
}