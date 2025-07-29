package com.ruparts.app.features.search.presentation

import com.ruparts.app.features.cart.model.CartListItem

class SearchScreenState(
    val items: List<CartListItem>,
    val filters: List<SearchScreenFilter>
)

class SearchScreenFilter(
    val text: String,
    val selected: Boolean
)