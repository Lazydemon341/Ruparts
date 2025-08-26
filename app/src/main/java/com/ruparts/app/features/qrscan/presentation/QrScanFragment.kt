package com.ruparts.app.features.qrscan.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.ruparts.app.core.ui.theme.RupartsTheme
import com.ruparts.app.core.ui.viewmodel.assistedViewModels
import com.ruparts.app.features.cart.presentation.CartFragment
import com.ruparts.app.features.cart.presentation.CartFragment.Companion.CART_TOAST_TO_SHOW_KEY
import com.ruparts.app.features.qrscan.presentation.model.QrScanScreenEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

enum class QrScanType {
    LOCATION_TO_CART,
    CART_TO_LOCATION,
    BOTH
}

@AndroidEntryPoint
class QrScanFragment : Fragment() {

    private val args: QrScanFragmentArgs by navArgs()
    private val viewModel by assistedViewModels<QrScanViewModel, QrScanViewModel.Factory> {
        create(scanType = args.scanType)
    }

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
                            is QrScanScreenEvent.NavigateBack -> {
                                if (event.updateCart || !event.toastToShow.isNullOrEmpty()) {
                                    setFragmentResult(
                                        CartFragment.CART_UPDATED_REQUEST_KEY,
                                        bundleOf(
                                            CART_TOAST_TO_SHOW_KEY to event.toastToShow,
                                        ),
                                    )
                                }
                                findNavController().popBackStack()
                            }

                            is QrScanScreenEvent.ShowErrorToast -> {
                                val message = event.message ?: "Не удалось отсканировать штрихкод"
                                snackBarJob?.cancel()
                                snackBarJob = coroutineScope.launch {
                                    snackbarHostState.showSnackbar(message)
                                }
                            }
                        }
                    }
                }

                RupartsTheme {
                    QrScanScreen(
                        state = state.value,
                        onAction = viewModel::handleAction,
                        snackbarHostState = snackbarHostState,
                    )
                }
            }
        }
    }
}

