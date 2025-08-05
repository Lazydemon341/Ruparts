package com.ruparts.app.features.productscan.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.findNavController
import com.ruparts.app.core.ui.theme.RupartsTheme
import com.ruparts.app.features.productscan.presentation.model.ProductScanScreenEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductScanFragment : Fragment() {

    private val viewModel: ProductScanViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val state = viewModel.state.collectAsStateWithLifecycle()
                val snackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    var snackBarJob: Job? = null

                    viewModel.events.collect { event ->
                        when (event) {
                            is ProductScanScreenEvent.NavigateBack -> {
                                findNavController().popBackStack()
                            }

                            is ProductScanScreenEvent.ShowErrorToast -> {
                                snackBarJob?.cancel()
                                snackBarJob = coroutineScope.launch {
                                    snackbarHostState.showSnackbar(event.message)
                                }
                            }

                            is ProductScanScreenEvent.NavigateToProductDetails -> {
                                val action = ProductScanFragmentDirections
                                    .actionProductScanFragmentToProductFragment(event.barcode)
                                findNavController().navigate(action)
                            }
                        }
                    }
                }

                RupartsTheme {
                    ProductScanScreen(
                        state = state.value,
                        onAction = viewModel::handleAction,
                        snackbarHostState = snackbarHostState,
                    )
                }
            }
        }
    }
}