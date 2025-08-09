package com.ruparts.app.features.search.presentation

import androidx.compose.runtime.Immutable
import com.ruparts.app.features.commonlibrary.ProductFlag
import java.util.UUID

@Immutable
sealed interface SearchScreenState {
    data object Loading : SearchScreenState

    data object Error : SearchScreenState

    @Immutable
    data class Content(
        val filters: List<SearchScreenFilter>,
        val flags: List<SearchScreenFlag>,
        val searchSets: List<SearchScreenSearchSet>,
        val selectedSorting: SearchScreenSorting,
        val locationFilter: String,
        val searchSetsText: String,
    ) : SearchScreenState
}

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
    val checked: Boolean,
) {
    // for preview purposes
    internal constructor(text: String, checked: Boolean) : this(
        ProductFlag(UUID.randomUUID().mostSignificantBits, text, ""), checked,
    )
}

data class SearchScreenSearchSet(
    val id: Long,
    val text: String,
    val supportingText: String,
    val checked: Boolean,
) {

    // for preview purposes
    internal constructor(text: String, supportingText: String, checked: Boolean) : this(
        UUID.randomUUID().mostSignificantBits,
        text,
        supportingText,
        checked,
    )
}

data class SearchScreenSorting(
    val type: SearchScreenSortingType = SearchScreenSortingType.QUANTITY,
    val direction: SortingDirection = SortingDirection.DESCENDING,
)

enum class SearchScreenSortingType {
    VENDOR_CODE,
    BRAND,
    LOCATION,
    QUANTITY,
}

enum class SortingDirection {
    ASCENDING,
    DESCENDING,
}