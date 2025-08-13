package com.ruparts.app.features.assembly.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.features.assembly.presentation.model.AssemblyScreenEffect
import com.ruparts.app.features.assembly.presentation.model.AssemblyScreenEvent
import com.ruparts.app.features.cart.model.CartListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssemblyViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<AssemblyScreenState>(
        AssemblyScreenState.Content(
            selectedTab = AssemblyTab.LIST,
            assemblyGroups = listOf(
                AssemblyGroup(
                    locationId = "L2-A01-1-6-1",
                    locationName = "L2-A01-1-6-1",
                    completedCount = 18,
                    totalCount = 128,
                    items = listOf(
                        AssemblyItem(
                            id = 1,
                            barcode = "TE23010111123456XJL",
                            article = "123456",
                            brand = "GENERAL MOTORS",
                            description = "Замок зажигания",
                            quantity = 133,
                        ),
                        AssemblyItem(
                            id = 2,
                            barcode = "A20250521T214852WVE",
                            article = "214852",
                            brand = "GENERAL MOTORS",
                            description = "Фильтр масляный",
                            quantity = 88
                        ),
                        AssemblyItem(
                            id = 3,
                            barcode = "A20250521T214852QK5",
                            article = "214852",
                            brand = "GENERAL MOTORS",
                            description = "Прокладка головки блока",
                            quantity = 34
                        )
                    )
                ),
                AssemblyGroup(
                    locationId = "K1-B04-3-2-6",
                    locationName = "K1-B04-3-2-6",
                    completedCount = 3,
                    totalCount = 45,
                    items = listOf(
                        AssemblyItem(
                            id = 4,
                            barcode = "TE23010111123456XJL",
                            article = "123456",
                            brand = "GENERAL MOTORS",
                            description = "Замок зажигания очень длинное описание очень...",
                            quantity = 14
                        )
                    )
                ),
                AssemblyGroup(
                    locationId = "L3-C02-1-3-4",
                    locationName = "L3-C02-1-3-4",
                    completedCount = 8,
                    totalCount = 90,
                    items = listOf(
                        AssemblyItem(
                            id = 5,
                            barcode = "TE23010111123456XJL",
                            article = "123456",
                            brand = "GENERAL MOTORS",
                            description = "Замок зажигания очень длинное описания",
                            quantity = 139
                        )
                    )
                )
            ),
            basketItems = listOf(
                CartListItem(
                    id = 1,
                    article = "123457879654531",
                    brand = "Toyota",
                    quantity = 125,
                    description = "Замок зажигания",
                    barcode = "TE250630T235959II2",
                    cartOwner = "Petrov",
                    info = "L2-A02-1-6-1",
                    flags = listOf(),
                    fromExternalInput = false
                ),
                CartListItem(
                    id = 2,
                    article = "987654321",
                    brand = "Honda",
                    quantity = 50,
                    description = "Фильтр воздушный",
                    barcode = "TE250630T235959II3",
                    cartOwner = "Ivanov",
                    info = "L2-A02-1-6-1",
                    flags = listOf(),
                    fromExternalInput = false
                ),
                CartListItem(
                    id = 3,
                    article = "456789012",
                    brand = "Nissan",
                    quantity = 200,
                    description = "Тормозные колодки",
                    barcode = "TE250630T235959II4",
                    cartOwner = "Sidorov",
                    info = "L3-C01-1-2-3",
                    flags = listOf(),
                    fromExternalInput = false
                )
            ),
        )
    )
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AssemblyScreenEffect>()
    val effect = _effect.asSharedFlow()

    fun handleEvent(event: AssemblyScreenEvent) {
        when (event) {
            is AssemblyScreenEvent.OnTabClick -> onTabClick(event.tab)
            is AssemblyScreenEvent.OnItemClick -> sendEffect(AssemblyScreenEffect.NavigateToItemDetails(event.item))
            is AssemblyScreenEvent.OnLocationClick -> onLocationClick(event.item)
            is AssemblyScreenEvent.OnDeleteClick -> onDeleteClick(event.item)
            is AssemblyScreenEvent.OnScanClick -> sendEffect(AssemblyScreenEffect.NavigateToScan)
        }
    }

    private fun sendEffect(effect: AssemblyScreenEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    private fun onTabClick(tab: AssemblyTab) {
        val currentState = _state.value
        if (currentState is AssemblyScreenState.Content) {
            _state.value = currentState.copy(selectedTab = tab)
        }
    }

    private fun onLocationClick(item: AssemblyItem) {
        // TODO
    }

    private fun onDeleteClick(item: AssemblyItem) {
        val currentState = _state.value
        if (currentState is AssemblyScreenState.Content) {
            val updatedGroups = currentState.assemblyGroups.map { group ->
                group.copy(
                    items = group.items.filter { it.id != item.id }
                )
            }.filter { it.items.isNotEmpty() }
            _state.value = currentState.copy(assemblyGroups = updatedGroups)
        }
    }
}