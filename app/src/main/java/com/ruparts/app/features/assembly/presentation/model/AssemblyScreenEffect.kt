package com.ruparts.app.features.assembly.presentation.model

import com.ruparts.app.features.assembly.presentation.AssemblyItem
import com.ruparts.app.features.assembly.presentation.AssemblyTab

sealed interface AssemblyScreenEffect {
    data object NavigateBack : AssemblyScreenEffect
    data class NavigateToItemDetails(val item: AssemblyItem) : AssemblyScreenEffect
    data class NavigateToScan(val selectedTab: AssemblyTab) : AssemblyScreenEffect
}