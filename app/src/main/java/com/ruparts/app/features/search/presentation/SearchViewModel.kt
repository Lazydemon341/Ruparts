package com.ruparts.app.features.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.features.cart.model.CartListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private val productFlags = listOf(
    SearchScreenFlag("Требуется измерить", false),
    SearchScreenFlag("Требуется взвесить", false),
    SearchScreenFlag("Требуется фото", false),
    SearchScreenFlag("Риск подделки", false),
    SearchScreenFlag("Продажа в розницу", false),
    SearchScreenFlag("Неликвид", false),
    SearchScreenFlag("Габаритный", false),
    SearchScreenFlag("Хрупкий", false),
    SearchScreenFlag("Загружен с Фрозы", false),
    SearchScreenFlag("Без документов", false),
)

private val INITIAL_STATE = SearchScreenState(
    items = listOf(
        CartListItem(
            id = 1,
            article = "123457879654531",
            brand = "Toyota",
            quantity = 125,
            description = "Замок зажигания",
            barcode = "TE250630T235959II2",
            cartOwner = "Petrov",
            info = "L2-A02-1-6-1",
            flags = listOf(productFlags[1].flag, productFlags[3].flag),
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
            flags = listOf(productFlags[0].flag, productFlags[2].flag),
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
            flags = listOf(productFlags[2].flag),
            fromExternalInput = false
        )
    ),
    filters = listOf(
        SearchScreenFilter(SearchScreenFilterType.FLAGS, false),
        SearchScreenFilter(SearchScreenFilterType.LOCATION, true),
        SearchScreenFilter(SearchScreenFilterType.SELECTIONS, false)
    ),
    flags = productFlags,
    selections = listOf(
        SearchScreenSelection("Для бухгалтерии", "ID 3, Петров Н.А., 14.06.2025", false),
        SearchScreenSelection("Срочно", "ID 69, Иванов В.М., 28.05.2025", false),
        SearchScreenSelection("Для доставки", "ID 25, Сидоров Г.Д., 21.05.2025", false),
        SearchScreenSelection("Для менеджера", "ID 73, Судоровозражанов Н.А., 14.06.2025", false),
    )
)

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {

    private val checkedFlags = MutableStateFlow<Set<Long>>(emptySet())
    private val selectedSorting = MutableStateFlow(SearchScreenSorting())
    private val locationFilterState = MutableStateFlow("")

    val state = combine(
        flow { emit(INITIAL_STATE) },
        checkedFlags,
        selectedSorting,
        locationFilterState,
    ) { state, flags, sorting, locationFilter ->
        state.copy(
            items = state.items
                .filterByFlags(flags)
                .filterByLocation(locationFilter)
                .sortBy(sorting),
            filters = listOf(
                SearchScreenFilter(SearchScreenFilterType.FLAGS, flags.isNotEmpty()),
                SearchScreenFilter(SearchScreenFilterType.LOCATION, locationFilter.isNotEmpty()),
                SearchScreenFilter(SearchScreenFilterType.SELECTIONS, false) // TODO
            ),
            checkedFlags = flags,
            selectedSorting = sorting,
            // TODO: filter also by selection
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchScreenState(emptyList(), emptyList(), emptyList(), emptyList()),
    )

    fun filterByFlags(flags: Set<Long>) {
        checkedFlags.value = flags
    }

    fun filterByLocation(location: String) {
        locationFilterState.value = location
    }

    fun clearFilter(filter: SearchScreenFilter) {
        when (filter.type) {
            SearchScreenFilterType.FLAGS -> checkedFlags.value = emptySet()
            SearchScreenFilterType.LOCATION -> locationFilterState.value = ""
            SearchScreenFilterType.SELECTIONS -> {} //TODO()
        }
    }

    fun setSorting(type: SearchScreenSortingType, direction: SortingDirection) {
        selectedSorting.value = SearchScreenSorting(type, direction)
    }
}

private fun List<CartListItem>.filterByFlags(flags: Set<Long>): List<CartListItem> {
    return filter { item ->
        flags.all { flagId ->
            item.flags.any { it.id == flagId }
        }
    }
}

private fun List<CartListItem>.filterByLocation(locationFilter: String): List<CartListItem> {
    if (locationFilter.isEmpty()) return this
    return filter { item ->
        item.cartOwner.contains(locationFilter, ignoreCase = true)
    }
}

private fun List<CartListItem>.sortBy(sorting: SearchScreenSorting): List<CartListItem> {
    return when (sorting.type) {
        SearchScreenSortingType.CELL_NUMBER -> {
            // Using info field which contains location data like "L2-A02-1-6-1"
            if (sorting.direction == SortingDirection.ASCENDING) {
                sortedBy { it.info }
            } else {
                sortedByDescending { it.info }
            }
        }

        SearchScreenSortingType.QUANTITY -> {
            if (sorting.direction == SortingDirection.ASCENDING) {
                sortedBy { it.quantity }
            } else {
                sortedByDescending { it.quantity }
            }
        }

        SearchScreenSortingType.PURCHASE_PRICE -> {
            // Using article as placeholder for purchase price until actual field is available
            if (sorting.direction == SortingDirection.ASCENDING) {
                sortedBy { it.article }
            } else {
                sortedByDescending { it.article }
            }
        }

        SearchScreenSortingType.SELLING_PRICE -> {
            // Using brand as placeholder for selling price until actual field is available
            if (sorting.direction == SortingDirection.ASCENDING) {
                sortedBy { it.brand }
            } else {
                sortedByDescending { it.brand }
            }
        }

        SearchScreenSortingType.ARRIVAL_DATE -> {
            // Using id as placeholder for arrival date until actual field is available
            if (sorting.direction == SortingDirection.ASCENDING) {
                sortedBy { it.id }
            } else {
                sortedByDescending { it.id }
            }
        }
    }
}