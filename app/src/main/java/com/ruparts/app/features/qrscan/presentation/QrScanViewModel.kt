package com.ruparts.app.features.qrscan.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.common.Barcode
import com.ruparts.app.features.cart.data.repository.CartRepository
import com.ruparts.app.features.cart.model.CartListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QrScanViewModel @Inject constructor(
    private val cartRepository: CartRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(QrScanScreenState(emptyList(), false))
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<QrScanScreenEvent>()
    val events = _events.asSharedFlow()

    private var scannedCodes = mutableSetOf<String>()

    fun handleAction(action: QrScanScreenAction) = viewModelScope.launch {
        when (action) {
            QrScanScreenAction.BackClick -> _events.emit(QrScanScreenEvent.NavigateBack)
            is QrScanScreenAction.BarcodesScanned -> onBarcodesScanned(action.barcodes)
            is QrScanScreenAction.RemoveItem -> onRemoveItem(action.item)
            is QrScanScreenAction.ManualInput -> onManualInput(action.code)
        }
    }

    private fun onRemoveItem(item: CartListItem) {
        _state.update {
            it.copy(scannedItems = it.scannedItems.filter { it.id != item.id })
        }
    }

    private fun onBarcodesScanned(barcodes: List<Barcode>) {
        if (state.value.isLoading) {
            return
        }

        val uniqueBarcodes = barcodes.filter { barcode ->
            barcode.rawValue !in scannedCodes
        }
        if (uniqueBarcodes.isEmpty()) {
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            uniqueBarcodes.forEach { barcode ->
                val rawValue = barcode.rawValue
                if (rawValue != null) {
                    doScan(rawValue)
                }
            }
            _state.update {
                it.copy(isLoading = false)
            }
        }
    }

    private fun onManualInput(code: String) = viewModelScope.launch {
        _state.update {
            it.copy(isLoading = true)
        }
        doScan(code)
        _state.update {
            it.copy(isLoading = false)
        }
    }

    private suspend fun doScan(code: String) {
        if (code in scannedCodes) {
            return
        }

        cartRepository.doScan(
            barcode = code,
        ).fold(
            onSuccess = { scannedItem ->
                onItemScanSuccess(scannedItem)
                scannedCodes.add(code)
            },
            onFailure = {
                // TODO
            }
        )
    }

    private fun onItemScanSuccess(scannedItem: CartListItem) {
        val alreadyScanned = state.value.scannedItems.find { it.id == scannedItem.id } != null
        val scannedItems = if (!alreadyScanned) {
            state.value.scannedItems.toMutableList().apply {
                add(scannedItem)
            }
        } else {
            state.value.scannedItems
        }
        _state.update {
            it.copy(
                scannedItems = scannedItems,
            )
        }
    }
}