package com.ruparts.app.features.qrscan.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.common.Barcode
import com.ruparts.app.features.qrscan.model.ScannedItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QrScanViewModel @Inject constructor() : ViewModel() {

    private val _scannedItems = MutableStateFlow(mockScannedItems)
    val scannedItems = _scannedItems.asStateFlow()

    private val _events = MutableSharedFlow<QrScanScreenEvent>()
    val events = _events.asSharedFlow()

    fun handleAction(action: QrScanScreenAction) = viewModelScope.launch {
        when (action) {
            QrScanScreenAction.BackClick -> _events.emit(QrScanScreenEvent.NavigateBack)
            is QrScanScreenAction.BarcodesScanned -> onBarcodesScanned(action.barcodes)
            is QrScanScreenAction.RemoveItem -> onRemoveItem(action.item)
            QrScanScreenAction.KeyboardClick -> _events.emit(QrScanScreenEvent.ShowKeyboardInput)
        }
    }

    private fun onRemoveItem(item: ScannedItem) {
        _scannedItems.value = _scannedItems.value.toMutableList().apply { remove(item) }
    }

    private fun onBarcodesScanned(barcodes: List<Barcode>) {

    }
}

val mockScannedItems = mutableListOf(
    ScannedItem(
        article = "11115555669987452131",
        brand = "Toyota",
        quantity = 13481,
        description = "Описание",
    ),
    ScannedItem(
        article = "548870578",
        brand = "Mazda",
        quantity = 10,
        description = "Длинное описание, которое не влезает в одну строчку",
    ),
    ScannedItem(
        article = "36575",
        brand = "Porsche",
        quantity = 5843,
        description = "Очень длинное описание, которое не влезает в одну строчку, которое не влезает в одну строчку, которое не влезает в одну строчку, которое не влезает в одну строчку,",
    )
)