package com.ruparts.app.features.qrscan.presentation

sealed interface QrScanScreenEvent {
    data object NavigateBack : QrScanScreenEvent
    data class ShowErrorToast(val message: String?) : QrScanScreenEvent
}