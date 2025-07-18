package com.ruparts.app.features.cart.presentation

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.core.barcode.BarcodeType
import com.ruparts.app.features.cart.data.repository.CartRepository
import com.ruparts.app.features.cart.data.repository.CartScanException
import com.ruparts.app.features.cart.model.CartListItem
import com.ruparts.app.features.cart.model.CartScanPurpose
import com.ruparts.app.features.cart.presentation.model.CartScreenEffect
import com.ruparts.app.features.cart.presentation.model.CartScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SCANNED_ITEM_TRANSFER_DELAY = 20_000L

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: CartRepository,
) : ViewModel() {

    private val isLoading = MutableStateFlow(false)
    private val reloadRequests = MutableSharedFlow<Unit>()

    private val _effects = MutableSharedFlow<CartScreenEffect>()
    val effects = _effects.asSharedFlow()

    private val scannedItemState = MutableStateFlow<CartListItem?>(null)
    private var scannedItemTransferJob: Job? = null

    private val _loaderState = MutableStateFlow(0f)
    val loaderState = _loaderState.asStateFlow()

    val state = combine(
        isLoading,
        cartItemsFlow()
    ) { isLoading, cartItems ->
        CartScreenState(
            items = cartItems,
            isLoading = isLoading,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = initialScreenState,
    )

    fun reloadCart() {
        reloadRequests.tryEmit(Unit)
    }

    fun onExternalCodeReceived(code: String, type: BarcodeType) {
        when (type) {
            BarcodeType.PRODUCT -> viewModelScope.launch {
                val codeInCart = state.value.items.any { it.barcode == code }
                scanExternalCode(code, codeInCart)
            }

            BarcodeType.LOCATION,
            BarcodeType.UNKNOWN -> Unit
        }
    }

    fun cancelScannedItemTransfer() {
        scannedItemTransferJob?.cancel()
        scannedItemState.value = null
    }

    private suspend fun scanExternalCode(code: String, isInCart: Boolean) {
        repository.scanProduct(
            barcode = code,
            purpose = if (isInCart) CartScanPurpose.TRANSFER_TO_LOCATION else CartScanPurpose.TRANSFER_TO_CART
        ).fold(
            onSuccess = { scannedItem ->
                if (isInCart) {
                    onItemInCartScanSuccess(scannedItem)
                } else {
                    onNewItemScanSuccess(scannedItem.copy(fromExternalInput = true))
                }
            },
            onFailure = { error ->
                onItemScanFailure(code, error)
            }
        )
    }

    private fun onItemInCartScanSuccess(item: CartListItem) = viewModelScope.launch {
        _effects.emit(CartScreenEffect.OpenTransferToLocationScreen(item))
    }

    private suspend fun onNewItemScanSuccess(scannedItem: CartListItem) {
        val currentScannedItem = scannedItemState.value
        if (currentScannedItem != null && scannedItemTransferJob?.isActive == true) {
            // new item scanned while previous item is still loading.
            // in this case we immediately transfer previous scanned item to basket.
            scannedItemTransferJob?.cancel()
            transferToCart(currentScannedItem)
        }

        scannedItemState.value = scannedItem

        scannedItemTransferJob = viewModelScope.launch {

            // wait for 20 seconds (user can cancel transfer during this time), then transfer to cart
            waitBeforeTransferringScannedItem()

            transferToCart(scannedItem)

            scannedItemState.value = null
        }
    }

    /**
     * Waits for 20 seconds before transferring scanned item to cart.
     * Also updates [loaderState] roughly each 16 ms (~60 fps) to show cancel button loading animation
     */
    private suspend fun waitBeforeTransferringScannedItem() {
        _loaderState.value = 0f
        val startTime = SystemClock.uptimeMillis()
        while (currentCoroutineContext().isActive) {
            val currentTime = SystemClock.uptimeMillis()
            val elapsed = currentTime - startTime
            val progress = (elapsed.toFloat() / SCANNED_ITEM_TRANSFER_DELAY).coerceIn(0f, 1f)

            _loaderState.value = progress

            if (progress >= 1f) {
                break
            }

            // ~16ms for 60fps
            delay(16)
        }
    }

    private suspend fun transferToCart(item: CartListItem) {
        repository.transferToCart(
            barcodes = listOf(item.barcode),
        ).fold(
            onSuccess = {
                // TODO
            },
            onFailure = { error ->
                // TODO show error toast
            }
        )
        reloadRequests.emit(Unit)
    }

    private fun onItemScanFailure(code: String, error: Throwable) {
        val errorMessage = when (error) {
            is CartScanException -> {
                error.message
            }

            else -> {
                null
            }
        }
        // TODO:
        // viewModelScope.launch {
        //     _effects.emit(CartScreenEffect.ShowErrorToast(errorMessage))
        // }
    }

    private fun cartItemsFlow(): Flow<List<CartListItem>> {
        val cartItemsFlow = reloadRequests
            .onStart { emit(Unit) }
            .map { repository.getCart().getOrThrow() }
        return combine(
            cartItemsFlow,
            scannedItemState
        ) { cartItems, scannedItem ->
            if (scannedItem != null) {
                cartItems + scannedItem
            } else {
                cartItems
            }.reversed()
        }
    }
}

private val initialScreenState = CartScreenState(
    items = emptyList(),
    isLoading = true
)
