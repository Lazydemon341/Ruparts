package com.ruparts.app.features.assembling.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ruparts.app.core.ui.theme.RupartsTheme
import com.ruparts.app.features.cart.model.CartListItem
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class AssemblingFragment : Fragment() {
    private val viewModel: AssemblingViewModel by viewModels()

    private val initialValue = listOf(
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
            info = "L1-B03-2-4-2",
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
        ))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                RupartsTheme {
                    AssemblingScreen(initialValue)
                }
            }
        }
    }

}