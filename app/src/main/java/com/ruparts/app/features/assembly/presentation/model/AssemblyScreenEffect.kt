package com.ruparts.app.features.assembly.presentation.model

import com.ruparts.app.features.assembly.presentation.AssemblyItem
import com.ruparts.app.features.assembly.presentation.AssemblyTab
import com.ruparts.app.features.qrscan.presentation.QrScanType

sealed interface AssemblyScreenEffect {
    data object NavigateBack : AssemblyScreenEffect
    data class NavigateToItemDetails(val item: AssemblyItem) : AssemblyScreenEffect
    data class NavigateToScan(val qrScanType: QrScanType) : AssemblyScreenEffect
}