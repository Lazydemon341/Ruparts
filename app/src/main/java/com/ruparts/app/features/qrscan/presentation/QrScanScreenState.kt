package com.ruparts.app.features.qrscan.presentation

import com.ruparts.app.features.cart.model.CartListItem

data class QrScanScreenState(
    val scannedItems: List<CartListItem>,
    val isLoading: Boolean,
)