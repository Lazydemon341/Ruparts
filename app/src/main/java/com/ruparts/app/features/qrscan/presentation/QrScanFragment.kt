package com.ruparts.app.features.qrscan.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
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
                val itemsState = viewModel.scannedItems.collectAsState()
                QrScanScreen(
                    scannedItems = itemsState.value, onRemove = {item -> viewModel.onRemove(item)}
                )
            }
        }
    }
}

