package com.ruparts.app.features.cart.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ruparts.app.R
import com.ruparts.app.core.ui.utils.alignAboveSystemBars
import com.ruparts.app.features.cart.model.CartListItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment() {

    private lateinit var initialTextView: TextView
    private lateinit var addButton: FloatingActionButton

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CartListAdapter

    val mockItems = null

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
//        ))


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
        addButton.alignAboveSystemBars()

        addButton.setOnClickListener {
            findNavController().navigate(CartFragmentDirections.actionCartFragmentToQrScanFragment())
        }

        recyclerView = view.findViewById(R.id.cart_recycler_view)
        adapter = CartListAdapter(
            onItemClick = { listItem ->
                findNavController().navigate(
                CartFragmentDirections.actionCartFragmentToCartItemFragment(listItem)
                )
            }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.submitList(mockItems)
//        initialTextView.isVisible = false
    }

}