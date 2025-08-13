package com.ruparts.app.features.assembly.presentation.model

import com.ruparts.app.features.assembly.presentation.AssemblyItem

sealed interface AssemblyScreenEffect {
    data object NavigateBack : AssemblyScreenEffect
    data class NavigateToItemDetails(val item: AssemblyItem) : AssemblyScreenEffect
    data object NavigateToScan : AssemblyScreenEffect
}