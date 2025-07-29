package com.ruparts.app.features.search.presentation

import androidx.lifecycle.ViewModel
import com.ruparts.app.features.cart.model.CartListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

private val INITIAL_STATE = SearchScreenState(
    items =  listOf(
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
        SearchScreenFilter("Признаки", false),
        SearchScreenFilter("Расположение", true),
        SearchScreenFilter("Выборки", false)
    )
)

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel(){

    private val _state = MutableStateFlow(INITIAL_STATE)
    val state = _state.asStateFlow()
}