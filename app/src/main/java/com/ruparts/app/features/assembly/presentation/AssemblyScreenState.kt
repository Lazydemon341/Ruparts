package com.ruparts.app.features.assembly.presentation

import androidx.compose.runtime.Immutable
import com.ruparts.app.features.cart.model.CartListItem

@Immutable
sealed interface AssemblyScreenState {
    data object Loading : AssemblyScreenState
    data object Error : AssemblyScreenState

    @Immutable
    data class Content(
        val selectedTab: AssemblyTab,
        val assemblyGroups: List<AssemblyGroup>,
        val basketItems: List<CartListItem>,
    ) : AssemblyScreenState
}

@Immutable
data class AssemblyGroup(
    val locationId: String,
    val locationName: String,
    val completedCount: Int,
    val totalCount: Int,
    val items: List<AssemblyItem>,
    val isExpanded: Boolean = true,
)

@Immutable
data class AssemblyItem(
    val id: Long,
    val barcode: String,
    val article: String,
    val brand: String,
    val description: String,
    val quantity: Int,
)

enum class AssemblyTab {
    LIST,
    BASKET
}