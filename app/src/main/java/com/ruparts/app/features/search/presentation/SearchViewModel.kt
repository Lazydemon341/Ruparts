package com.ruparts.app.features.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ruparts.app.core.utils.combine
import com.ruparts.app.features.cart.model.CartListItem
import com.ruparts.app.features.commonlibrary.ProductFlag
import com.ruparts.app.features.commonlibrary.data.repository.CommonLibraryRepository
import com.ruparts.app.features.search.data.repository.SearchRepository
import com.ruparts.app.features.search.model.SearchSetItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val commonLibraryRepository: CommonLibraryRepository,
    private val searchRepository: SearchRepository,
) : ViewModel() {

    private val checkedFlags = MutableStateFlow<Set<Long>>(emptySet())
    private val checkedSearchSets = MutableStateFlow<Set<Long>>(emptySet())
    private val selectedSorting = MutableStateFlow(SearchScreenSorting())
    private val locationFilterState = MutableStateFlow("")
    private val _searchState = MutableStateFlow("")
    private val searchSetsState = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagedItems: Flow<PagingData<CartListItem>> = combine(
        checkedFlags,
        checkedSearchSets,
        selectedSorting,
        locationFilterState,
        _searchState,
    ) { checkedFlags, checkedSearchSets, sorting, locationFilter, search ->
        searchRepository.getListPaged(locationFilter, checkedFlags.toList(), checkedSearchSets.toList(), search, sorting)
    }.flatMapLatest { it }
        .cachedIn(viewModelScope)

    val state = combine(
        productFlagsFlow(),
        checkedFlags,
        searchSetsFlow(),
        checkedSearchSets,
        selectedSorting,
        locationFilterState,
        searchSetsState,
    ) { productFlags, checkedFlags, searchSets, checkedSearchSets, sorting, locationFilter, searchSetsText ->
        SearchScreenState.Content(
            filters = listOf(
                SearchScreenFilter(SearchScreenFilterType.FLAGS, checkedFlags.isNotEmpty()),
                SearchScreenFilter(SearchScreenFilterType.LOCATION, locationFilter.isNotEmpty()),
                SearchScreenFilter(SearchScreenFilterType.SELECTIONS, checkedSearchSets.isNotEmpty())
            ),
            flags = mapFlags(productFlags, checkedFlags),
            searchSets = mapSearchSets(searchSets, checkedSearchSets),
            selectedSorting = sorting,
            locationFilter = locationFilter,
            searchSetsText = searchSetsText,
        )
    }.catch<SearchScreenState> {
        emit(SearchScreenState.Error)
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

    fun updateSearchText(text: String) {
        _searchState.value = text
    }

    fun updateSearchSetsText(text: String) {
        searchSetsState.value = text
    }

    private fun productFlagsFlow() = flow {
        emit(commonLibraryRepository.getProductFlags())
    }

    private fun searchSetsFlow() = searchSetsState.map { searchText ->
        searchRepository.getSearchSets(searchText)
            .getOrDefault(emptyList())
    }

    private fun mapFlags(productFlags: Map<Long, ProductFlag>, checkedFlags: Set<Long>): List<SearchScreenFlag> {
        return productFlags.map { (_, flag) ->
            SearchScreenFlag(flag = flag, checked = flag.id in checkedFlags)
        }
    }

    private fun mapSearchSets(sets: List<SearchSetItem>, checked: Set<Long>): List<SearchScreenSearchSet> {
        return sets.map { set ->
            SearchScreenSearchSet(
                id = set.id,
                supportingText = set.author,
                text = set.title,
                checked = set.id in checked,
            )
        }
    }
}