package com.ruparts.app.features.qrscan.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.common.Barcode
import com.ruparts.app.core.barcode.BarcodeType
import com.ruparts.app.core.barcode.BarcodeTypeDetector
import com.ruparts.app.features.cart.data.repository.CartRepository
import com.ruparts.app.features.cart.data.repository.CartScanException
import com.ruparts.app.features.cart.model.CartListItem
import com.ruparts.app.features.cart.model.CartScanPurpose
import com.ruparts.app.features.qrscan.presentation.model.QrScanScreenAction
import com.ruparts.app.features.qrscan.presentation.model.QrScanScreenEvent
import com.ruparts.app.features.qrscan.presentation.model.QrScanScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private val INITIAL_STATE = QrScanScreenState(
    emptyList(),
    false,
    CartScanPurpose.TRANSFER_TO_CART,
)

@HiltViewModel
class QrScanViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val barcodeTypeDetector: BarcodeTypeDetector,
) : ViewModel() {

    private val _state = MutableStateFlow(INITIAL_STATE)
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<QrScanScreenEvent>()
    val events = _events.asSharedFlow()

    private var scannedCodes = mutableSetOf<String>()
    private var firstItemScanned: Boolean = false

    fun handleAction(action: QrScanScreenAction) = viewModelScope.launch {
        when (action) {
            QrScanScreenAction.BackClick -> _events.emit(QrScanScreenEvent.NavigateBack())
            is QrScanScreenAction.BarcodesScanned -> onBarcodesScanned(action.barcodes)
            is QrScanScreenAction.RemoveItem -> onRemoveItem(action.item)
            is QrScanScreenAction.ManualInput -> onManualInput(action.code)
            QrScanScreenAction.OnTransferToCart -> transferToCart()
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

//            uniqueBarcodes.forEach { barcode ->
//                val rawValue = barcode.rawValue
//                if (rawValue != null) {
//                    doScan(rawValue)
//                }
//            }

            val barcode = uniqueBarcodes.first { !it.rawValue.isNullOrEmpty() }
            doScan(requireNotNull(barcode.rawValue))

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
        if (
            state.value.purpose == CartScanPurpose.TRANSFER_TO_LOCATION
            && state.value.scannedItems.isNotEmpty()
            && barcodeTypeDetector.detectCodeType(code) == BarcodeType.LOCATION
        ) {
            transferToLocation(code)
            return
        }

        if (code in scannedCodes) {
            return
        }

        checkScanPurpose(code)
        scanProduct(code)
    }

    private suspend fun scanProduct(code: String) {
        cartRepository.scanProduct(
            barcode = code,
            purpose = state.value.purpose,
        ).fold(
            onSuccess = { scannedItem ->
                onItemScanSuccess(scannedItem)
                scannedCodes.add(code)
                firstItemScanned = true
            },
            onFailure = { error ->
                onItemScanFailure(code, error)
            }
        )
    }

    private fun onItemScanSuccess(scannedItem: CartListItem) {
        val alreadyScanned = state.value.scannedItems.find { it.id == scannedItem.id } != null
        if (!alreadyScanned) {
            val scannedItems = state.value.scannedItems.toMutableList().apply {
                add(scannedItem)
            }
            _state.update {
                it.copy(
                    scannedItems = scannedItems,
                )
            }
        }
    }

    private fun onItemScanFailure(code: String, error: Throwable) {
        val errorMessage = when (error) {
            is CartScanException -> {
                scannedCodes.add(code)

                error.message
            }

            else -> {
                null
            }
        }
        viewModelScope.launch {
            _events.emit(QrScanScreenEvent.ShowErrorToast(errorMessage))
        }
    }

    private fun transferToCart() {
        if (state.value.isLoading) {
            return
        }

        _state.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            cartRepository.transferToCart(
                barcodes = state.value.scannedItems.map { it.barcode },
            ).fold(
                onSuccess = {
                    _events.emit(QrScanScreenEvent.NavigateBack(updateCart = true))
                },
                onFailure = { error ->
                    // TODO show error toast
                }
            )
        }
        _state.update {
            it.copy(isLoading = false)
        }
    }

    private suspend fun checkScanPurpose(code: String) {
        if (firstItemScanned) {
            return
        }

        val cartItems = cartRepository.getCart().getOrDefault(emptyList())
        if (cartItems.any { it.barcode == code }) {
            _state.update {
                it.copy(purpose = CartScanPurpose.TRANSFER_TO_LOCATION)
            }
        } else {
            _state.update {
                it.copy(purpose = CartScanPurpose.TRANSFER_TO_CART)
            }
        }
    }

    private suspend fun transferToLocation(locationCode: String) {
        cartRepository.transferToLocation(
            barcodes = state.value.scannedItems.map { it.barcode },
            location = locationCode,
        ).fold(
            onSuccess = {
                _events.emit(
                    QrScanScreenEvent.NavigateBack(
                        updateCart = true,
                        toastToShow = "Товары размещены в $locationCode"
                    )
                )
            },
            onFailure = { error ->
                // TODO
            }
        )
    }
}