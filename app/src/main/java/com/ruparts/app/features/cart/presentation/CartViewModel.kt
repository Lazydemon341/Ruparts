package com.ruparts.app.features.cart.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.features.cart.data.repository.CartRepository
import com.ruparts.app.features.cart.model.CartListItem
import com.ruparts.app.features.cart.presentation.model.CartScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: CartRepository
) : ViewModel() {

    private val isLoading = MutableStateFlow(false)
    private val reloadRequests = MutableSharedFlow<Unit>()

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

    private fun cartItemsFlow(): Flow<List<CartListItem>> {
        return reloadRequests
            .onStart { emit(Unit) }
            .map { repository.getCart().getOrThrow() }
    }
}

private val initialScreenState = CartScreenState(
    items = emptyList(),
    isLoading = true
)
