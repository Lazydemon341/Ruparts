package com.ruparts.app.features.productscan.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.core.barcode.TrackBarcodeFocusUseCase
import com.ruparts.app.features.cart.data.repository.CartRepository
import com.ruparts.app.features.cart.data.repository.CartScanException
import com.ruparts.app.features.cart.model.CartScanPurpose
import com.ruparts.app.features.productscan.model.ProductScanType
import com.ruparts.app.features.productscan.presentation.model.ProductScanScreenAction
import com.ruparts.app.features.productscan.presentation.model.ProductScanScreenEvent
import com.ruparts.app.features.productscan.presentation.model.ProductScanScreenState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ProductScanViewModel.Factory::class)
class ProductScanViewModel @AssistedInject constructor(
    @Assisted private val scanType: ProductScanType,
    private val cartRepository: CartRepository,
    private val trackBarcodeFocusUseCase: TrackBarcodeFocusUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ProductScanScreenState(scanType = this.scanType))
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
                    performScan(action.input)
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
        performScan(barcode)
        _state.update {
            it.copy(isScanning = false)
        }
    }

    private suspend fun performScan(code: String) {
        when (scanType) {
            ProductScanType.PRODUCT -> {
                cartRepository.scanProduct(
                    barcode = code,
                    purpose = CartScanPurpose.INFO,
                ).fold(
                    onSuccess = { scannedItem ->
                        onProductScanSuccess(scannedItem.barcode)
                    },
                    onFailure = { error ->
                        onScanFailure(code, error)
                    }
                )
            }

            ProductScanType.LOCATION -> {
                cartRepository.scanLocation(barcode = code, purpose = CartScanPurpose.INFO).fold(
                    onSuccess = {
                        onLocationScanSuccess(code)
                    },
                    onFailure = { error ->
                        onScanFailure(code, error)
                    }
                )
            }
        }
    }

    private suspend fun onProductScanSuccess(barcode: String) {
        _events.emit(ProductScanScreenEvent.NavigateToProductDetails(barcode))
    }

    private suspend fun onLocationScanSuccess(barcode: String) {
        _events.emit(ProductScanScreenEvent.LocationScanSuccess(barcode))
    }

    private suspend fun onScanFailure(code: String, error: Throwable) {
        val errorMessage = (error as? CartScanException)?.message ?: "Не удалось отсканировать штрихкод $code"
        _events.emit(ProductScanScreenEvent.ShowErrorToast(errorMessage))
    }

    @AssistedFactory
    interface Factory {
        fun create(scanType: ProductScanType): ProductScanViewModel
    }
}