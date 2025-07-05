package com.ruparts.app.features.qrscan.presentation

import androidx.lifecycle.ViewModel
import com.ruparts.app.features.qrscan.model.ScannedItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class QrScanViewModel @Inject constructor() : ViewModel() {

    private val _scannedItems = MutableStateFlow(mockScannedItems)
    val scannedItems = _scannedItems.asStateFlow()

    fun onRemove(item: ScannedItem) {
        _scannedItems.value = _scannedItems.value.toMutableList().apply { remove(item) }
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