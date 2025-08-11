package com.ruparts.app.features.search.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.ruparts.app.R
import com.ruparts.app.core.barcode.BarcodeType
import com.ruparts.app.core.barcode.ExternalCodeInputHandler
import com.ruparts.app.core.ui.theme.RupartsTheme
import com.ruparts.app.features.productscan.model.ProductScanType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModels()

    private val externalCodeInputHandler = ExternalCodeInputHandler { code, type ->
        if (type == BarcodeType.PRODUCT) {
            findNavController().navigate(
                SearchFragmentDirections.actionSearchFragmentToProductFragment(code)
            )
        }
    }

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.search_menu, menu)
            val searchItem = menu.findItem(R.id.search_bar)
            val searchView = searchItem.actionView as? SearchView
            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.updateSearchText(query ?: "")
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.updateSearchText(newText ?: "")
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

        setFragmentResultListener("location_scan_result") { _, bundle ->
            val scannedLocation = bundle.getString("scanned_location")
            if (scannedLocation != null) {
                viewModel.filterByLocation(scannedLocation)
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                val state = viewModel.state.collectAsStateWithLifecycle()
                val pagedItems = viewModel.pagedItems.collectAsLazyPagingItems()
                RupartsTheme {
                    SearchScreen(
                        state = state.value,
                        pagedItems = pagedItems,
                        onSubmitFlags = viewModel::filterByFlags,
                        onSubmitSearchSets = viewModel::filterBySearchSets,
                        onScanButtonClick = {
                            findNavController().navigate(
                                SearchFragmentDirections.actionSearchFragmentToProductScanFragment(ProductScanType.PRODUCT)
                            )
                        },
                        onClearFilter = viewModel::clearFilter,
                        onItemClick = { item ->
                            findNavController().navigate(
                                SearchFragmentDirections.actionSearchFragmentToProductFragment(item.barcode)
                            )
                        },
                        onSortingSelect = viewModel::setSorting,
                        onLocationFilter = viewModel::filterByLocation,
                        onLocationScanClick = {
                            findNavController().navigate(
                                SearchFragmentDirections.actionSearchFragmentToProductScanFragment(ProductScanType.LOCATION)
                            )
                        },
                        onKeyEvent = { event ->
                            externalCodeInputHandler.onKeyEvent(event)
                        },
                        onSearchSetsTextChange = viewModel::updateSearchSetsText
                    )
                }
            }
        }
    }
}