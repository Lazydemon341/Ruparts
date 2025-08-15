package com.ruparts.app.features.search.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.findNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.ruparts.app.MainActivity
import com.ruparts.app.R
import com.ruparts.app.core.ui.theme.RupartsTheme
import com.ruparts.app.features.search.presentation.model.SearchScreenEffect
import com.ruparts.app.features.search.presentation.model.SearchScreenEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModels()

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            createMenu(menu, menuInflater)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)

        setFragmentResultListener("location_scan_result") { _, bundle ->
            val scannedLocation = bundle.getString("scanned_location")
            if (scannedLocation != null) {
                viewModel.handleEvent(SearchScreenEvent.FilterByLocation(scannedLocation))
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                val state = viewModel.state.collectAsStateWithLifecycle()
                val pagedItems = viewModel.pagedItems.collectAsLazyPagingItems()
                RupartsTheme {
                    LaunchedEffect(Unit) {
                        viewModel.effect.collectLatest { effect ->
                            when (effect) {
                                is SearchScreenEffect.NavigateToScan -> {
                                    findNavController().navigate(
                                        SearchFragmentDirections.actionSearchFragmentToProductScanFragment(effect.scanType)
                                    )
                                }

                                is SearchScreenEffect.NavigateToProduct -> {
                                    findNavController().navigate(
                                        SearchFragmentDirections.actionSearchFragmentToProductFragment(effect.barcode)
                                    )
                                }

                                is SearchScreenEffect.NavigateToAssembly -> {
                                    findNavController().navigate(
                                        SearchFragmentDirections.actionSearchFragmentToAssemblyFragment()
                                    )
                                }
                            }
                        }
                    }

                    SearchScreen(
                        state = state.value,
                        pagedItems = pagedItems,
                        onEvent = viewModel::handleEvent,
                        onKeyEvent = viewModel::handleKeyEvent,
                    )
                }
            }
        }
    }

    private fun createMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.search_bar)
        val checkmarkItem = menu.findItem(R.id.checkmark)
        //val menuDotsItem = menu.findItem(R.id.menu_dots)

        searchItem.setOnActionExpandListener(onActionExpandListener(checkmarkItem))
        checkmarkItem.setOnActionExpandListener(onActionExpandListener(searchItem))

        (searchItem.actionView as? SearchView)?.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.handleEvent(SearchScreenEvent.UpdateSearchText(query ?: ""))
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.handleEvent(SearchScreenEvent.UpdateSearchText(newText ?: ""))
                    return true
                }
            })
        }
    }

    private fun onActionExpandListener(itemToHide: MenuItem): MenuItem.OnActionExpandListener {
        return object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                itemToHide.isVisible = false
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                itemToHide.isVisible = true
                (requireActivity() as MainActivity).supportInvalidateOptionsMenu()
                return true
            }
        }
    }
}