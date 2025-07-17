package com.ruparts.app.features.qrscan.presentation.model

sealed interface QrScanScreenEvent {
    data class NavigateBack(
        val updateCart: Boolean = false,
        val toastToShow: String? = null,
    ) : QrScanScreenEvent
    data class ShowErrorToast(val message: String?) : QrScanScreenEvent
}