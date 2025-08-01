package com.ruparts.app.features.search.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.ruparts.app.core.ui.theme.RupartsTheme
import dagger.hilt.android.AndroidEntryPoint

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
                val state = viewModel.state.collectAsState()
                RupartsTheme {
                    SearchScreen(
                        state = state.value,
                        onScanButtonClick = {
                            findNavController().navigate(
                                SearchFragmentDirections.actionSearchFragmentToProductScanFragment()
                            )
                        }
                    )
                }
            }

        }
    }
}