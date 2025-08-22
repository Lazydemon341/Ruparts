package com.ruparts.app.features.assembly.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.fragment.findNavController
import com.ruparts.app.R
import com.ruparts.app.core.ui.theme.RupartsTheme
import com.ruparts.app.features.assembly.presentation.model.AssemblyScreenEffect
import com.ruparts.app.features.assembly.presentation.model.AssemblyScreenEvent
import com.ruparts.app.features.cart.presentation.CartFragmentDirections
import com.ruparts.app.features.qrscan.presentation.QrScanType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AssemblyFragment : Fragment() {

    private val viewModel: AssemblyViewModel by viewModels()

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.assembly_menu, menu)
            val searchItem = menu.findItem(R.id.search_bar)
            val searchView = searchItem.actionView as? SearchView
            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.handleEvent(AssemblyScreenEvent.OnSearchTextChange(query ?: ""))
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.handleEvent(AssemblyScreenEvent.OnSearchTextChange(newText ?: ""))
                    return true
                }
            })
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)

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

                                }

                                is AssemblyScreenEffect.NavigateToScan -> {
                                    if (effect.selectedTab == AssemblyTab.LIST) {
                                    findNavController().navigate(AssemblyFragmentDirections.actionAssemblyFragmentToQrScanFragment(
                                        QrScanType.LOCATION_TO_CART
                                    ))
                                    } else if (effect.selectedTab == AssemblyTab.BASKET) {
                                        findNavController().navigate(AssemblyFragmentDirections.actionAssemblyFragmentToQrScanFragment(
                                            QrScanType.CART_TO_LOCATION
                                        ))
                                    }
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