package com.ruparts.app.features.qrscan.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ruparts.app.features.qrscan.model.ScannedItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QrScanFragment : Fragment() {

    private val viewModel: QrScanViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                QrScanScreen(mockScannedItems)
            }
        }
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