package com.ruparts.app.features.cart.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.features.cart.data.repository.CartRepository
import com.ruparts.app.features.cart.model.CartListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: CartRepository
) : ViewModel() {

    private val isLoading = MutableStateFlow(false)
    private val cartItems = MutableStateFlow<List<CartListItem>>(emptyList())

    private var loadCartJob: Job? = null

    val state = flow {
        emit(repository.getCart().getOrNull() ?: emptyList())
    }.stateIn(
        scope = viewModelScope + Dispatchers.Default,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )

    fun loadCart() {
        loadCartJob?.cancel()
        loadCartJob = viewModelScope.launch {
            isLoading.value = true
            cartItems.value = repository.getCart()
                .getOrDefault(emptyList())!!
            isLoading.value = false
        }
    }


}

