package com.ruparts.app.features.qrscan.presentation.model

import com.ruparts.app.features.cart.model.CartListItem

data class QrScanScreenState(
    val scannedItems: List<CartListItem>,
    val isLoading: Boolean,
    val purpose: QrScanPurpose,
)