package com.ruparts.app.features.cart.presentation.transfertolocation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.core.barcode.BarcodeType
import com.ruparts.app.features.cart.data.repository.CartRepository
import com.ruparts.app.features.cart.data.repository.CartScanException
import com.ruparts.app.features.cart.model.CartListItem
import com.ruparts.app.features.cart.model.CartScanPurpose
import com.ruparts.app.features.cart.presentation.transfertolocation.model.CartTransferToLocationScreenEffect
import com.ruparts.app.features.cart.presentation.transfertolocation.model.CartTransferToLocationScreenState
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

@HiltViewModel(assistedFactory = CartTransferToLocationViewModel.Factory::class)
class CartTransferToLocationViewModel @AssistedInject constructor(
    @Assisted
    private val scannedItem: CartListItem,
    private val repository: CartRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(
        CartTransferToLocationScreenState(
            items = listOf(scannedItem),
        )
    )
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<CartTransferToLocationScreenEffect>()
    val effects = _effects.asSharedFlow()

    fun onExternalCodeReceived(code: String, type: BarcodeType) = viewModelScope.launch {
        when (type) {
            BarcodeType.PRODUCT -> scanProductCode(code)
            BarcodeType.LOCATION -> transferToLocation(code)
            BarcodeType.UNKNOWN -> Unit
        }
    }

    fun onRemoveItem(item: CartListItem) = viewModelScope.launch {
        val filteredItems = state.value.items.filter { it.id != item.id }
        _state.update {
            it.copy(items = filteredItems)
        }
        if (filteredItems.isEmpty()) {
            _effects.emit(CartTransferToLocationScreenEffect.NavigateBack())
        }
    }

    private suspend fun scanProductCode(code: String) {
        if (state.value.items.any { it.barcode == code }) {
            return
        }

        repository.scanProduct(
            barcode = code,
            purpose = CartScanPurpose.TRANSFER_TO_LOCATION,
        ).fold(
            onSuccess = { scannedItem ->
                _state.update {
                    it.copy(
                        items = it.items.plus(scannedItem)
                    )
                }
            },
            onFailure = { error ->
                if (error is CartScanException && !error.message.isNullOrEmpty()) {
                    _effects.emit(
                        CartTransferToLocationScreenEffect.ShowToast(requireNotNull(error.message))
                    )
                }
            }
        )
    }

    private suspend fun transferToLocation(locationCode: String) {
        repository.transferToLocation(
            barcodes = state.value.items.map { it.barcode },
            location = locationCode,
        ).fold(
            onSuccess = {
                _effects.emit(
                    CartTransferToLocationScreenEffect.NavigateBack(
                        updateCart = true,
                        toastToShow = "Товары размещены в $locationCode"
                    )
                )
            },
            onFailure = { error ->
                // TODO: show error
            }
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(scannedItem: CartListItem): CartTransferToLocationViewModel
    }
}
