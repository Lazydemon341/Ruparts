package com.ruparts.app.features.qrscan.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.ruparts.app.core.ui.theme.RupartsTheme
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

                LaunchedEffect(Unit) {
                    viewModel.events.collect { event ->
                        when (event) {
                            QrScanScreenEvent.NavigateBack -> {
                                findNavController().popBackStack()
                            }

                            QrScanScreenEvent.ShowKeyboardInput -> {

                            }
                        }
                    }
                }

                RupartsTheme {
                    QrScanScreen(
                        scannedItems = itemsState.value,
                        onAction = viewModel::handleAction,
                    )
                }
            }
        }
    }
}

