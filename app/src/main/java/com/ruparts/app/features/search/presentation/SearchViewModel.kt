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
            description = "Описание",
            barcode = "TE250630T235959II2",
            cartOwner = "Petrov",
            info = "",
            flags = listOf(productFlags[1].flag, productFlags[3].flag),
            fromExternalInput = false
        ),
        CartListItem(
            id = 2,
            article = "987654321",
            brand = "Honda",
            quantity = 50,
            description = "Другое описание",
            barcode = "TE250630T235959II2",
            cartOwner = "Ivanov",
            info = "",
            flags = listOf(productFlags[0].flag, productFlags[2].flag),
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

    val state = combine(
        flow<SearchScreenState> { emit(INITIAL_STATE) },
        checkedFlags,
    ) { state, flags ->
        state.copy(
            items = state.items.filter { item ->
                flags.all { flagId ->
                    item.flags.any { it.id == flagId }
                }
            },
            filters = listOf(
                SearchScreenFilter(SearchScreenFilterType.FLAGS, flags.isNotEmpty()),
                SearchScreenFilter(SearchScreenFilterType.LOCATION, false), // TODO
                SearchScreenFilter(SearchScreenFilterType.SELECTIONS, false) // TODO
            ),
            checkedFlags = flags,
            // TODO: filter also by text and selection
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchScreenState(emptyList(), emptyList(), emptyList(), emptyList()),
    )

    fun filterByFlags(flags: Set<Long>) {
        checkedFlags.value = flags
    }

    fun clearFilter(filter: SearchScreenFilter) {
        when (filter.type) {
            SearchScreenFilterType.FLAGS -> checkedFlags.value = emptySet()
            SearchScreenFilterType.LOCATION -> {} // TODO()
            SearchScreenFilterType.SELECTIONS -> {} //TODO()
        }
    }
}