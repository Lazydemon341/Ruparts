package com.ruparts.app.features.productscan.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.features.productscan.presentation.model.ProductScanScreenAction
import com.ruparts.app.features.productscan.presentation.model.ProductScanScreenEvent
import com.ruparts.app.features.productscan.presentation.model.ProductScanScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductScanViewModel @Inject constructor() : ViewModel() {

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
            
            is ProductScanScreenAction.OnFlashToggle -> {
                _state.value = _state.value.copy(
                    isFlashEnabled = !_state.value.isFlashEnabled
                )
            }
            
            is ProductScanScreenAction.OnProductScanned -> {
                _state.value = _state.value.copy(
                    scannedData = action.barcode,
                    isScanning = false
                )
                // TODO: Process scanned product and navigate to details
            }
            
            is ProductScanScreenAction.ManualInput -> {
                _state.value = _state.value.copy(
                    scannedData = action.input,
                    isScanning = false
                )
                // TODO: Process manually entered product code and navigate to details
            }
        }
    }
}