package com.ruparts.app.features.search.presentation.model

import com.ruparts.app.features.cart.model.CartListItem

sealed interface SearchScreenEvent {
    data class FilterByFlags(val flags: Set<Long>) : SearchScreenEvent
    data class FilterByLocation(val location: String) : SearchScreenEvent
    data class FilterBySearchSets(val searchSets: Set<Long>) : SearchScreenEvent
    data class ClearFilter(val filter: SearchScreenFilter) : SearchScreenEvent
    data class SetSorting(val type: SearchScreenSortingType, val direction: SortingDirection) : SearchScreenEvent
    data class UpdateSearchText(val text: String) : SearchScreenEvent
    data class UpdateSearchSetsText(val text: String) : SearchScreenEvent
    data object OnScanButtonClick : SearchScreenEvent
    data class OnItemClick(val item: CartListItem) : SearchScreenEvent
    data object OnLocationScanClick : SearchScreenEvent
    data object OnAssemblyClick : SearchScreenEvent
    data class ToggleSelectionMode(val enabled: Boolean) : SearchScreenEvent
    data class ToggleItemSelection(val itemId: Long) : SearchScreenEvent
}