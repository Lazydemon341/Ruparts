package com.ruparts.app.features.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.core.utils.combine
import com.ruparts.app.features.cart.model.CartListItem
import com.ruparts.app.features.commonlibrary.ProductFlag
import com.ruparts.app.features.commonlibrary.data.repository.CommonLibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val commonLibraryRepository: CommonLibraryRepository,
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
        // TODO: emit(repository.getSearchSets())
        emit(emptyList())
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
        // todo: searchRepository.getItems(...)
        return listOf(
            CartListItem(
                id = 1,
                article = "123457879654531",
                brand = "Toyota",
                quantity = 125,
                description = "Замок зажигания",
                barcode = "TE250630T235959II2",
                cartOwner = "Petrov",
                info = "L2-A02-1-6-1",
                flags = listOf(),
                fromExternalInput = false
            ),
            CartListItem(
                id = 2,
                article = "987654321",
                brand = "Honda",
                quantity = 50,
                description = "Фильтр воздушный",
                barcode = "TE250630T235959II3",
                cartOwner = "Ivanov",
                info = "L1-B03-2-4-2",
                flags = listOf(),
                fromExternalInput = false
            ),
            CartListItem(
                id = 3,
                article = "456789012",
                brand = "Nissan",
                quantity = 200,
                description = "Тормозные колодки",
                barcode = "TE250630T235959II4",
                cartOwner = "Sidorov",
                info = "L3-C01-1-2-3",
                flags = listOf(),
                fromExternalInput = false
            )
        )
    }
}