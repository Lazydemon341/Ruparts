package com.ruparts.app.features.cartItem.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.ruparts.app.R
import com.ruparts.app.core.ui.utils.paddingAboveSystemBars
import com.ruparts.app.features.cart.model.CartListItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartItemFragment : Fragment(){

    private lateinit var article: TextView
    private lateinit var brand: TextView
    private lateinit var quantity: TextView
    private lateinit var description: TextView
    private lateinit var barcode: TextView

    private val arguments: CartItemFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.product_unit, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.paddingAboveSystemBars()

        val cartItem: CartListItem = arguments.cartItem

        article = view.findViewById(R.id.article_value)
        brand = view.findViewById(R.id.brand_value)
        quantity = view.findViewById(R.id.quantity_value)
        description = view.findViewById(R.id.description_value)
        barcode = view.findViewById(R.id.barcode_value)

        article.text = cartItem.article
        brand.text = cartItem.brand
        quantity.text = cartItem.quantity.toString()
        description.text = cartItem.description
        barcode.text = cartItem.barcode

        setupMenu()

    }

    private fun setupMenu() {
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.product_unit_bottom_menu, menu)

                val unitMenu = menu.findItem(R.id.unit_menu)

                unitMenu.setOnMenuItemClickListener {
                    CartItemActionsFragment().show(parentFragmentManager, "")
                    return@setOnMenuItemClickListener true
                }

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner)
    }

}