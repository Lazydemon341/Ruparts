package com.ruparts.app.features.search.presentation

import androidx.compose.runtime.Immutable
import com.ruparts.app.features.cart.model.CartListItem

@Immutable
data class SearchScreenState(
    val items: List<CartListItem>,
    val filters: List<SearchScreenFilter>,
    val flags: List<SearchScreenFlag>,
    val selection: List<SearchScreenSelection>
)

data class SearchScreenFilter(
    val type: SearchScreenFilterType,
    val selected: Boolean,
)

enum class SearchScreenFilterType {
    FLAGS,
    LOCATION,
    SELECTIONS,
}

data class SearchScreenFlag(
    val text: String,
    var checked: Boolean,
)

data class SearchScreenSelection(
    val text: String,
    val supportingText: String,
    var checked: Boolean,
)