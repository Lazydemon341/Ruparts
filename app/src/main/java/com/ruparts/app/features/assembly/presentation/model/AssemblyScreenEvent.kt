package com.ruparts.app.features.assembly.presentation.model

import com.ruparts.app.features.assembly.presentation.AssemblyItem
import com.ruparts.app.features.assembly.presentation.AssemblyTab

sealed interface AssemblyScreenEvent {
    data class OnTabClick(val tab: AssemblyTab) : AssemblyScreenEvent
    data class OnItemClick(val item: AssemblyItem) : AssemblyScreenEvent
    data class OnLocationClick(val item: AssemblyItem) : AssemblyScreenEvent
    data class OnDeleteClick(val item: AssemblyItem) : AssemblyScreenEvent
    data object OnScanClick : AssemblyScreenEvent
    data class OnSearchTextChange(val text: String) : AssemblyScreenEvent
}