package com.ruparts.app.features.assembly.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.fragment.findNavController
import com.ruparts.app.core.ui.theme.RupartsTheme
import com.ruparts.app.features.assembly.presentation.model.AssemblyScreenEffect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AssemblyFragment : Fragment() {

    private val viewModel: AssemblyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val state by viewModel.state.collectAsState()

                LaunchedEffect(Unit) {
                    viewModel.effect
                        .flowWithLifecycle(lifecycle)
                        .collectLatest { effect ->
                            when (effect) {
                                is AssemblyScreenEffect.NavigateBack -> {
                                    findNavController().navigateUp()
                                }

                                is AssemblyScreenEffect.NavigateToItemDetails -> {
                                    // TODO: Navigate to item details
                                }

                                is AssemblyScreenEffect.NavigateToScan -> {
                                    // TODO: Navigate to scanner
                                }
                            }
                        }
                }

                RupartsTheme {
                    AssemblyScreen(
                        state = state,
                        onEvent = viewModel::handleEvent
                    )
                }
            }
        }
    }
}