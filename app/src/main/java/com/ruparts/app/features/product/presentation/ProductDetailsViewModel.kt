package com.ruparts.app.features.product.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.features.product.data.repository.ProductRepository
import com.ruparts.app.features.product.presentation.model.ProductDetailsScreenState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel(assistedFactory = ProductDetailsViewModel.Factory::class)
class ProductDetailsViewModel @AssistedInject constructor(
    @Assisted barcode: String,
    private val productRepository: ProductRepository,
) : ViewModel() {

    val screenState = flow {
        emit(productRepository.readProduct(barcode = barcode).getOrThrow())
    }.map { product ->
        ProductDetailsScreenState.Content(
            product = product,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProductDetailsScreenState.Loading,
    )

    @AssistedFactory
    interface Factory {
        fun create(barcode: String): ProductDetailsViewModel
    }
}