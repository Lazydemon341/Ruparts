package com.ruparts.app.features.assembly.presentation.model

import com.ruparts.app.features.assembly.presentation.AssemblyItem
import com.ruparts.app.features.assembly.presentation.AssemblyTab

sealed interface AssemblyScreenEvent {
    data object OnBackClick : AssemblyScreenEvent
    data object OnSearchClick : AssemblyScreenEvent
    data object OnMenuClick : AssemblyScreenEvent
    data class OnTabClick(val tab: AssemblyTab) : AssemblyScreenEvent
    data class OnItemClick(val item: AssemblyItem) : AssemblyScreenEvent
    data class OnFavoriteClick(val item: AssemblyItem) : AssemblyScreenEvent
    data class OnDeleteClick(val item: AssemblyItem) : AssemblyScreenEvent
    data object OnScanClick : AssemblyScreenEvent
}