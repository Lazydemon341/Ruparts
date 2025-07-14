package com.ruparts.app.features.qrscan.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.ruparts.app.core.ui.theme.RupartsTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

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
                val state = viewModel.state.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    var snackBarJob: Job? = null

                    viewModel.events.collect { event ->
                        when (event) {
                            QrScanScreenEvent.NavigateBack -> {
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

