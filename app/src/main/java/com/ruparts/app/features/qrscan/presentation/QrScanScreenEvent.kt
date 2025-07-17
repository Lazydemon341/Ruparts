package com.ruparts.app.features.qrscan.presentation

sealed interface QrScanScreenEvent {
    data class NavigateBack(
        val updateCart: Boolean = false,
    ) : QrScanScreenEvent
    data class ShowErrorToast(val message: String?) : QrScanScreenEvent
}