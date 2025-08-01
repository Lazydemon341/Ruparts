package com.ruparts.app.features.productscan.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.core.barcode.TrackBarcodeFocusUseCase
import com.ruparts.app.features.cart.data.repository.CartRepository
import com.ruparts.app.features.cart.data.repository.CartScanException
import com.ruparts.app.features.cart.model.CartScanPurpose
import com.ruparts.app.features.productscan.presentation.model.ProductScanScreenAction
import com.ruparts.app.features.productscan.presentation.model.ProductScanScreenEvent
import com.ruparts.app.features.productscan.presentation.model.ProductScanScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductScanViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val trackBarcodeFocusUseCase: TrackBarcodeFocusUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ProductScanScreenState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<ProductScanScreenEvent>()
    val events = _events.asSharedFlow()

    fun handleAction(action: ProductScanScreenAction) {
        when (action) {
            is ProductScanScreenAction.OnBackPressed -> {
                viewModelScope.launch {
                    _events.emit(ProductScanScreenEvent.NavigateBack)
                }
            }

            is ProductScanScreenAction.ManualInput -> {
                viewModelScope.launch {
                    _state.update { it.copy(isScanning = true) }
                    scanProduct(action.input)
                    _state.update { it.copy(isScanning = false) }
                }
            }

            is ProductScanScreenAction.BarcodesScanned -> {
                onBarcodesScanned(action.barcodes)
            }
        }
    }

    private fun onBarcodesScanned(barcodes: List<String>) = viewModelScope.launch {
        if (state.value.isScanning) {
            return@launch
        }

        val barcode = barcodes.firstOrNull() ?: return@launch

        // Track this barcode for focus duration requirement
        if (trackBarcodeFocusUseCase.trackBarcode(barcode)) {
            processBarcodeAfterFocus(barcode)
        }
    }

    private suspend fun processBarcodeAfterFocus(barcode: String) {
        _state.update {
            it.copy(isScanning = true)
        }
        scanProduct(barcode)
        _state.update {
            it.copy(isScanning = false)
        }
    }

    private suspend fun scanProduct(code: String) {
        cartRepository.scanProduct(
            barcode = code,
            purpose = CartScanPurpose.INFO,
        ).fold(
            onSuccess = { scannedItem ->
                onItemScanSuccess(scannedItem.barcode)
            },
            onFailure = { error ->
                onItemScanFailure(code, error)
            }
        )
    }

    private suspend fun onItemScanSuccess(barcode: String) {
        _events.emit(ProductScanScreenEvent.NavigateToProductDetails(barcode))
    }

    private suspend fun onItemScanFailure(code: String, error: Throwable) {
        val errorMessage = (error as? CartScanException)?.message ?: "Не удалось отсканировать штрихкод $code"
        _events.emit(ProductScanScreenEvent.ShowErrorToast(errorMessage))
    }
}