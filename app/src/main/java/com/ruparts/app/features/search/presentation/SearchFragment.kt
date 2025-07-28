package com.ruparts.app.features.search.presentation

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
class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                RupartsTheme {
                    SearchScreen(
                        searchItems = listOf(
                            CartListItem(
                                id = 1,
                                article = "123457879654531",
                                brand = "Toyota",
                                quantity = 125,
                                description = "Описание",
                                barcode = "hhjruhturt",
                                cartOwner = "Petrov",
                                info = "",
                                fromExternalInput = false
                            ),
                            CartListItem(
                                id = 2,
                                article = "987654321",
                                brand = "Honda",
                                quantity = 50,
                                description = "Другое описание",
                                barcode = "barcode-here",
                                cartOwner = "Ivanov",
                                info = "",
                                fromExternalInput = false
                            )
                        )
                    )
                }
            }

        }
    }
}