package com.ruparts.app.features.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.core.utils.combine
import com.ruparts.app.features.cart.model.CartListItem
import com.ruparts.app.features.commonlibrary.ProductFlag
import com.ruparts.app.features.commonlibrary.data.repository.CommonLibraryRepository
import com.ruparts.app.features.search.data.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val commonLibraryRepository: CommonLibraryRepository,
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val checkedFlags = MutableStateFlow<Set<Long>>(emptySet())
    private val checkedSearchSets = MutableStateFlow<Set<Long>>(emptySet())
    private val selectedSorting = MutableStateFlow(SearchScreenSorting())
    private val locationFilterState = MutableStateFlow("")

    val state = combine(
        productFlagsFlow(),
        checkedFlags,
        searchSetsFlow(),
        checkedSearchSets,
        selectedSorting,
        locationFilterState,
    ) { productFlags, checkedFlags, searchSets, checkedSearchSets, sorting, locationFilter ->
        SearchScreenState.Content(
            items = getItems(checkedFlags, checkedSearchSets, sorting, locationFilter),
            filters = listOf(
                SearchScreenFilter(SearchScreenFilterType.FLAGS, checkedFlags.isNotEmpty()),
                SearchScreenFilter(SearchScreenFilterType.LOCATION, locationFilter.isNotEmpty()),
                SearchScreenFilter(SearchScreenFilterType.SELECTIONS, searchSets.isNotEmpty())
            ),
            flags = mapFlags(productFlags, checkedFlags),
            searchSets = searchSets,
            selectedSorting = sorting,
            locationFilter = locationFilter,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchScreenState.Loading,
    )

    fun filterByFlags(flags: Set<Long>) {
        checkedFlags.value = flags
    }

    fun filterByLocation(location: String) {
        locationFilterState.value = location
    }

    fun filterBySearchSets(searchSets: Set<Long>) {
        checkedSearchSets.value = searchSets
    }

    fun clearFilter(filter: SearchScreenFilter) {
        when (filter.type) {
            SearchScreenFilterType.FLAGS -> checkedFlags.value = emptySet()
            SearchScreenFilterType.LOCATION -> locationFilterState.value = ""
            SearchScreenFilterType.SELECTIONS -> checkedSearchSets.value = emptySet()
        }
    }

    fun setSorting(type: SearchScreenSortingType, direction: SortingDirection) {
        selectedSorting.value = SearchScreenSorting(type, direction)
    }

    private fun productFlagsFlow() = flow {
        emit(commonLibraryRepository.getProductFlags())
    }

    private fun searchSetsFlow() = flow<List<SearchScreenSearchSet>> {
        emit(searchRepository.getSearchSets().getOrDefault(emptyList()))
    }


    private fun mapFlags(productFlags: Map<Long, ProductFlag>, checkedFlags: Set<Long>): List<SearchScreenFlag> {
        return productFlags.map { (_, flag) ->
            SearchScreenFlag(flag = flag, checked = flag.id in checkedFlags)
        }
    }

    private suspend fun getItems(
        checkedFlags: Set<Long>,
        checkedSearchSets: Set<Long>,
        sorting: SearchScreenSorting,
        locationFilter: String,
    ): List<CartListItem> {
        return searchRepository.getList(
            checkedFlags.toList(),
            checkedSearchSets.toList(),
            locationFilter, sorting
        ).getOrDefault(emptyList())
    }
}