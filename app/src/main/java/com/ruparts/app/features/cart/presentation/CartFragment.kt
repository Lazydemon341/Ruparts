package com.ruparts.app.features.cart.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.ruparts.app.R
import com.ruparts.app.core.ui.utils.alignAboveSystemBars
import com.ruparts.app.core.ui.utils.dp
import com.ruparts.app.core.ui.utils.enableEdgeToEdge
import com.ruparts.app.core.utils.collectWhileStarted
import com.ruparts.app.features.cart.presentation.model.CartScreenState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment() {

    private val viewModel: CartViewModel by viewModels()

    private lateinit var initialTextView: TextView
    private lateinit var addButton: FloatingActionButton

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CartListAdapter

    private lateinit var progressIndicator: CircularProgressIndicator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialTextView = view.findViewById(R.id.empty_cart)
        addButton = view.findViewById(R.id.add_button)
        addButton.alignAboveSystemBars(
            right = 20.dp(requireContext()),
            bottom = 24.dp(requireContext()),
        )

        addButton.setOnClickListener {
            findNavController().navigate(CartFragmentDirections.actionCartFragmentToQrScanFragment())
        }

        progressIndicator = view.findViewById(R.id.cart_progress_indicator)

        recyclerView = view.findViewById(R.id.cart_recycler_view)
        recyclerView.enableEdgeToEdge()
        adapter = CartListAdapter(
            onItemClick = { listItem ->
                findNavController().navigate(
                    CartFragmentDirections.actionCartFragmentToCartItemFragment(listItem)
                )
            }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        observeScreenState()
        setupTaskUpdateListener()
    }

    private fun observeScreenState() {
        viewModel.state.collectWhileStarted(viewLifecycleOwner) { state ->
            updateUI(state)
        }
    }

    private fun updateUI(state: CartScreenState) {
        when (state.isLoading) {
            true -> {
                recyclerView.isVisible = false
                initialTextView.isVisible = false
                progressIndicator.isVisible = true
            }

            false -> {
                recyclerView.isVisible = state.items.isNotEmpty()
                initialTextView.isVisible = state.items.isEmpty()
                progressIndicator.isVisible = false
            }
        }
        setToolbarSubtitle(
            if (state.items.isEmpty()) {
                null
            } else {
                val itemsCount = state.items.size
                resources.getQuantityString(
                    R.plurals.cart_items_count,
                    itemsCount,
                    itemsCount,
                )
            }
        )
        adapter.submitList(state.items)
    }

    private fun setupTaskUpdateListener() {
        setFragmentResultListener(CART_UPDATED_REQUEST_KEY) { _, _ ->
            viewModel.reloadCart()
        }
    }

    private fun setToolbarSubtitle(subtitle: String?) {
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = subtitle
    }

    companion object {
        const val CART_UPDATED_REQUEST_KEY = "cart_updated_request_key"
    }
}

