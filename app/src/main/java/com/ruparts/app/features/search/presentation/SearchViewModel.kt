package com.ruparts.app.features.search.presentation

import androidx.lifecycle.ViewModel
import com.ruparts.app.features.cart.model.CartListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

private val INITIAL_STATE = SearchScreenState(
    items = listOf(
        CartListItem(
            id = 1,
            article = "123457879654531",
            brand = "Toyota",
            quantity = 125,
            description = "Описание",
            barcode = "hhjruhturt",
            cartOwner = "Petrov",
            info = "",
            fromExternalInput = false
        ),
        CartListItem(
            id = 2,
            article = "987654321",
            brand = "Honda",
            quantity = 50,
            description = "Другое описание",
            barcode = "barcode-here",
            cartOwner = "Ivanov",
            info = "",
            fromExternalInput = false
        )
    ),
    filters = listOf(
        SearchScreenFilter(SearchScreenFilterType.FLAGS, false),
        SearchScreenFilter(SearchScreenFilterType.LOCATION, true),
        SearchScreenFilter(SearchScreenFilterType.SELECTIONS, false)
    ),
    flags = listOf(
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
    ),
    selection = listOf(
        SearchScreenSelection("Для бухгалтерии", "ID 3, Петров Н.А., 14.06.2025", false),
        SearchScreenSelection("Срочно", "ID 69, Иванов В.М., 28.05.2025", false),
        SearchScreenSelection("Для доставки", "ID 25, Сидоров Г.Д., 21.05.2025", false),
        SearchScreenSelection("Для менеджера", "ID 73, Судоровозражанов Н.А., 14.06.2025", false),
    )
)

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(INITIAL_STATE)
    val state = _state.asStateFlow()
}