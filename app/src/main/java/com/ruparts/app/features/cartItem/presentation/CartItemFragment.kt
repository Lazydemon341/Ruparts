package com.ruparts.app.features.cartItem.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ruparts.app.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartItemFragment : Fragment(){

    private lateinit var article: TextView
    private lateinit var brand: TextView
    private lateinit var quantity: TextView
    private lateinit var description: TextView
    private lateinit var barcode: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.product_unit, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        article = view.findViewById(R.id.article_value)
        brand = view.findViewById(R.id.brand_value)
        quantity = view.findViewById(R.id.quantity_value)
        description = view.findViewById(R.id.description_value)
        barcode = view.findViewById(R.id.barcode_value)

    }

}