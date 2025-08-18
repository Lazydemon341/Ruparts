package com.ruparts.app.features.qrscan.presentation.model

import com.ruparts.app.features.cart.model.CartListItem
import com.ruparts.app.features.cart.model.CartScanPurpose
import com.ruparts.app.features.qrscan.presentation.QrScanType

data class QrScanScreenState(
    val scannedItems: List<CartListItem>,
    val isLoading: Boolean,
    val purpose: CartScanPurpose,
    val scanType: QrScanType
)