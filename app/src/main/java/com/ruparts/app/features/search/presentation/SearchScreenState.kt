package com.ruparts.app.features.search.presentation

import androidx.compose.runtime.Immutable
import com.ruparts.app.features.cart.model.CartListItem
import com.ruparts.app.features.commonlibrary.ProductFlag
import java.util.UUID

@Immutable
data class SearchScreenState(
    val items: List<CartListItem>,
    val filters: List<SearchScreenFilter>,
    val flags: List<SearchScreenFlag>,
    val selections: List<SearchScreenSelection>,
    val checkedFlags: Set<Long> = emptySet(),
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
    val flag: ProductFlag,
) {

    // for preview purposes
    internal constructor(text: String, checked: Boolean) : this(
        ProductFlag(UUID.randomUUID().mostSignificantBits, text, ""),
    )
}

data class SearchScreenSelection(
    val id: Long,
    val text: String,
    val supportingText: String,
    val checked: Boolean = false,
) {

    // for preview purposes
    internal constructor(text: String, supportingText: String, checked: Boolean) : this(
        UUID.randomUUID().mostSignificantBits,
        text,
        supportingText,
        checked,
    )
}