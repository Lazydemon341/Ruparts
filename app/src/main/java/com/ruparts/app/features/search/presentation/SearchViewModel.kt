package com.ruparts.app.features.search.presentation

import android.view.KeyEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ruparts.app.core.barcode.BarcodeType
import com.ruparts.app.core.barcode.ExternalCodeInputHandler
import com.ruparts.app.core.utils.combine
import com.ruparts.app.features.cart.model.CartListItem
import com.ruparts.app.features.commonlibrary.data.repository.CommonLibraryRepository
import com.ruparts.app.features.productscan.model.ProductScanType
import com.ruparts.app.features.search.data.repository.SearchRepository
import com.ruparts.app.features.search.presentation.model.SearchScreenEffect
import com.ruparts.app.features.search.presentation.model.SearchScreenEvent
import com.ruparts.app.features.search.presentation.model.SearchScreenFilter
import com.ruparts.app.features.search.presentation.model.SearchScreenFilterType
import com.ruparts.app.features.search.presentation.model.SearchScreenFlag
import com.ruparts.app.features.search.presentation.model.SearchScreenMode
import com.ruparts.app.features.search.presentation.model.SearchScreenSearchSet
import com.ruparts.app.features.search.presentation.model.SearchScreenSorting
import com.ruparts.app.features.search.presentation.model.SearchScreenSortingType
import com.ruparts.app.features.search.presentation.model.SearchScreenState
import com.ruparts.app.features.search.presentation.model.SortingDirection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
    private val searchState = MutableStateFlow("")
    private val searchSetsText = MutableStateFlow("")
    private val searchMode = MutableStateFlow(SearchScreenMode.SEARCH)
    private val selectedItems = MutableStateFlow<Set<Long>>(emptySet())

    private val _effect = MutableSharedFlow<SearchScreenEffect>()
    val effect = _effect.asSharedFlow()

    private val externalCodeInputHandler = ExternalCodeInputHandler { code, type ->
        if (type == BarcodeType.PRODUCT) {
            sendEffect(SearchScreenEffect.NavigateToProduct(code))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagedItems: Flow<PagingData<CartListItem>> = combine(
        checkedFlags,
        checkedSearchSets,
        selectedSorting,
        locationFilterState,
        searchState,
    ) { checkedFlags, checkedSearchSets, sorting, locationFilter, search ->
        searchRepository.getListPaged(
            locationFilter = locationFilter,
            flags = checkedFlags.toList(),
            sets = checkedSearchSets.toList(),
            search = search,
            sorting = sorting,
        )
    }.flatMapLatest { it }
        .cachedIn(viewModelScope)

    val state = combine(
        productFlagsFlow(),
        searchSetsFlow(),
        selectedSorting,
        locationFilterState,
        searchSetsText,
        filtersFlow(),
        searchMode,
        selectedItems,
    ) { productFlags, searchSets, sorting, locationFilter, searchSetsText, filters, mode, selectedItems ->
        SearchScreenState.Content(
            mode = mode,
            filters = filters,
            flags = productFlags,
            searchSets = searchSets,
            selectedSorting = sorting,
            locationFilter = locationFilter,
            searchSetsText = searchSetsText,
            selectedItems = selectedItems,
        )
    }.catch<SearchScreenState> {
        emit(SearchScreenState.Error)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchScreenState.Loading,
    )

    fun handleEvent(event: SearchScreenEvent) {
        when (event) {
            is SearchScreenEvent.FilterByFlags -> filterByFlags(event.flags)
            is SearchScreenEvent.FilterByLocation -> filterByLocation(event.location)
            is SearchScreenEvent.FilterBySearchSets -> filterBySearchSets(event.searchSets)
            is SearchScreenEvent.ClearFilter -> clearFilter(event.filter)
            is SearchScreenEvent.SetSorting -> setSorting(event.type, event.direction)
            is SearchScreenEvent.UpdateSearchText -> updateSearchText(event.text)
            is SearchScreenEvent.UpdateSearchSetsText -> updateSearchSetsText(event.text)
            is SearchScreenEvent.OnScanButtonClick -> sendEffect(SearchScreenEffect.NavigateToScan(ProductScanType.PRODUCT))
            is SearchScreenEvent.OnItemClick -> handleItemClick(event.item)
            is SearchScreenEvent.OnLocationScanClick -> sendEffect(SearchScreenEffect.NavigateToScan(ProductScanType.LOCATION))
            is SearchScreenEvent.OnAssemblyClick -> sendEffect(SearchScreenEffect.NavigateToAssembly)
            is SearchScreenEvent.ToggleSelectionMode -> toggleSelectionMode(event.enabled)
            is SearchScreenEvent.ToggleItemSelection -> toggleItemSelection(event.itemId)
        }
    }

    fun handleKeyEvent(keyEvent: KeyEvent): Boolean {
        return externalCodeInputHandler.onKeyEvent(keyEvent)
    }

    private fun sendEffect(effect: SearchScreenEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

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
        searchState.value = text
    }

    fun updateSearchSetsText(text: String) {
        searchSetsText.value = text
    }

    private fun productFlagsFlow() = combine(
        flow { emit(commonLibraryRepository.getProductFlags()) },
        checkedFlags,
    ) { flags, checked ->
        flags.map { (_, flag) ->
            SearchScreenFlag(
                flag = flag,
                checked = flag.id in checked
            )
        }
    }

    private fun searchSetsFlow() = combine(
        searchSetsText,
        checkedSearchSets
    ) { searchText, checked ->
        searchRepository.getSearchSets(searchText)
            .getOrDefault(emptyList())
            .map { set ->
                SearchScreenSearchSet(
                    id = set.id,
                    supportingText = set.author,
                    text = set.title,
                    checked = set.id in checked,
                )
            }
    }

    private fun filtersFlow() = combine(
        checkedFlags.map { it.isNotEmpty() },
        checkedSearchSets.map { it.isNotEmpty() },
        locationFilterState.map { it.isNotEmpty() },
    ) { hasFlags, hasSearchSets, hasLocation ->
        listOf(
            SearchScreenFilter(SearchScreenFilterType.FLAGS, hasFlags),
            SearchScreenFilter(SearchScreenFilterType.LOCATION, hasLocation),
            SearchScreenFilter(SearchScreenFilterType.SELECTIONS, hasSearchSets)
        )
    }

    private fun toggleSelectionMode(enabled: Boolean) {
        searchMode.value = if (enabled) {
            SearchScreenMode.SELECTION
        } else {
            selectedItems.value = emptySet()
            SearchScreenMode.SEARCH
        }
    }

    private fun toggleItemSelection(itemId: Long) {
        selectedItems.value = selectedItems.value.toMutableSet().apply {
            if (contains(itemId)) {
                remove(itemId)
            } else {
                add(itemId)
            }
        }
    }

    private fun handleItemClick(item: CartListItem) {
        if (searchMode.value == SearchScreenMode.SELECTION) {
            toggleItemSelection(item.id)
        } else {
            sendEffect(SearchScreenEffect.NavigateToProduct(item.barcode))
        }
    }
}