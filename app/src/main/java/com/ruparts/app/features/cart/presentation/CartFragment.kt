package com.ruparts.app.features.cart.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

//    val mockItems = listOf(
//        CartListItem(
//            article = "11115555669987452131",
//            brand = "Toyota",
//            quantity = 13481,
//            description = "Описание",
//            barcode = "H8676BDVGJBDERY",
//            cartOwner = "Ivanov A.A."
//        ),
//        CartListItem(
//            article = "548870578",
//            brand = "Mazda",
//            quantity = 10,
//            description = "Длинное описание, которое не влезает в одну строчку",
//            barcode = "JBHFT76YUT76567tFJJCXGVNK",
//            cartOwner = "Petrov P.P"
//        ),
//        CartListItem(
//            article = "36575",
//            brand = "Porsche",
//            quantity = 1265843,
//            description = "Очень длинное описание, которое не влезает в одну строчку, которое не влезает в одну строчку, которое не влезает в одну строчку, которое не влезает в одну строчку,",
//            barcode = "86HH765BVDR5533DG",
//            cartOwner = "Sidorov S.S."
//        )
//    )


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
        when(state.isLoading) {
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
        adapter.submitList(state.items)
    }

    private fun setupTaskUpdateListener() {
        setFragmentResultListener(CartFragment.CART_UPDATED_REQUEST_KEY) { _, _ ->
            viewModel.loadCart()
        }
    }

    companion object {
        const val CART_UPDATED_REQUEST_KEY = "cart_updated_request_key"
    }

}

