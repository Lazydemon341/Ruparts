package com.ruparts.app.features.product.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.ruparts.app.R
import com.ruparts.app.core.ui.utils.paddingAboveSystemBars
import com.ruparts.app.core.ui.viewmodel.assistedViewModels
import com.ruparts.app.core.utils.collectWhileStarted
import com.ruparts.app.core.utils.formatSafely
import com.ruparts.app.features.commonlibrary.ProductFlag
import com.ruparts.app.features.product.domain.ProductCard
import com.ruparts.app.features.product.domain.ProductDefect
import com.ruparts.app.features.product.domain.ProductPhotoItem
import com.ruparts.app.features.product.presentation.adapter.ProductFlagsAdapter
import com.ruparts.app.features.product.presentation.adapter.ProductPhotosAdapter
import com.ruparts.app.features.product.presentation.model.ProductDetailsScreenState
import dagger.hilt.android.AndroidEntryPoint
import java.time.format.DateTimeFormatter
import java.util.Locale


@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy").withLocale(Locale("ru", "RU"))

    private val args: ProductDetailsFragmentArgs by navArgs()
    private val viewModel by assistedViewModels<ProductDetailsViewModel, ProductDetailsViewModel.Factory> {
        create(barcode = args.barcode)
    }

    private lateinit var content: ViewGroup
    private lateinit var article: TextView
    private lateinit var brand: TextView
    private lateinit var quantity: TextView
    private lateinit var description: TextView
    private lateinit var barcode: TextView
    private lateinit var address: TextView
    private lateinit var date: TextView
    private lateinit var comment: TextView

    // Product photos and flags
    private lateinit var productPhotosRecyclerView: RecyclerView
    private lateinit var productFlagsRecyclerView: RecyclerView

    // Product card views
    private lateinit var productCardLayout: View
    private lateinit var weightValue: TextView
    private lateinit var lengthValue: TextView
    private lateinit var widthValue: TextView
    private lateinit var heightValue: TextView
    private lateinit var cardComment: TextView
    private lateinit var productCardPhotosRecyclerView: RecyclerView

    // Product defects views
    private lateinit var productDefectsLayout: View
    private lateinit var defectInfo: TextView
    private lateinit var productDefectPhotosRecyclerView: RecyclerView

    private lateinit var loadingIndicator: View

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

        content = view.findViewById(R.id.product_content)
        article = content.findViewById(R.id.article_value)
        brand = content.findViewById(R.id.brand_value)
        quantity = content.findViewById(R.id.quantity_value)
        description = content.findViewById(R.id.description_value)
        barcode = content.findViewById(R.id.barcode_value)
        address = content.findViewById(R.id.address_value)
        date = content.findViewById(R.id.date_value)
        comment = content.findViewById(R.id.comment_value)

        productPhotosRecyclerView = content.findViewById(R.id.product_photos)
        setupPhotosRecycler(productPhotosRecyclerView)

        productFlagsRecyclerView = content.findViewById(R.id.product_flags_list)
        setupFlagsRecycler(productFlagsRecyclerView)

        // Initialize product card views
        productCardLayout = content.findViewById(R.id.product_card)
        weightValue = content.findViewById(R.id.weight_value)
        lengthValue = content.findViewById(R.id.length_value)
        widthValue = content.findViewById(R.id.width_value)
        heightValue = content.findViewById(R.id.height_value)
        cardComment = content.findViewById(R.id.comment_info_value)

        productCardPhotosRecyclerView = content.findViewById(R.id.product_card_photos)
        setupPhotosRecycler(productCardPhotosRecyclerView)

        // Initialize product defects views
        productDefectsLayout = content.findViewById(R.id.product_defects)
        defectInfo = content.findViewById(R.id.defect_info)

        productDefectPhotosRecyclerView = content.findViewById(R.id.product_defect_photos)
        setupPhotosRecycler(productDefectPhotosRecyclerView)

        loadingIndicator = view.findViewById(R.id.loading_indicator)

        setupMenu()

        observeScreenState()
    }

    private fun setupMenu() {
        // TODO: uncomment when adding product editing feature
//        val menuProvider = object : MenuProvider {
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                menuInflater.inflate(R.menu.product_unit_bottom_menu, menu)
//
//                val unitMenu = menu.findItem(R.id.unit_menu)
//                unitMenu.setOnMenuItemClickListener {
//                    ProductActionsFragment().show(parentFragmentManager, "")
//                    return@setOnMenuItemClickListener true
//                }
//            }
//
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//                return false
//            }
//        }
//        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner)
    }

    private fun observeScreenState() {
        viewModel.screenState.collectWhileStarted(viewLifecycleOwner) { state ->
            updateUI(state)
        }
    }

    private fun updateUI(state: ProductDetailsScreenState) {
        when (state) {
            is ProductDetailsScreenState.Content -> {
                loadingIndicator.isVisible = false
                content.isVisible = true

                article.text = state.product.vendorCode
                brand.text = state.product.brand
                quantity.text = state.product.quantity.toString()
                description.text = state.product.description
                barcode.text = state.product.barcode
                address.text = state.product.location
                date.text = state.product.acceptedAt.formatSafely(dateFormatter)
                comment.text = state.product.unitComment

                updatePhotos(productPhotosRecyclerView, state.product.photos)
                updateProductFlags(state.product.flags)

                updateProductCard(state.product.card)
                updateProductDefects(state.product.defect)
            }

            ProductDetailsScreenState.Loading -> {
                loadingIndicator.isVisible = true
                content.isVisible = false
            }
        }
    }

    private fun updateProductCard(productCard: ProductCard?) {
        if (productCard == null) {
            productCardLayout.visibility = View.GONE
            return
        }
        productCardLayout.visibility = View.VISIBLE

        weightValue.text = productCard.weight?.let { "$it кг" } ?: "-"
        heightValue.text = productCard.sizeHeight?.let { "$it мм" } ?: "-"
        widthValue.text = productCard.sizeWidth?.let { "$it мм" } ?: "-"
        lengthValue.text = productCard.sizeLength?.let { "$it мм" } ?: "-"
        cardComment.text = productCard.comment

        updatePhotos(productCardPhotosRecyclerView, productCard.photos)
    }

    private fun updateProductDefects(productDefect: ProductDefect?) {
        if (productDefect == null || (productDefect.comment.isNullOrBlank() && productDefect.photos.isEmpty())) {
            productDefectsLayout.visibility = View.GONE
            return
        }
        productDefectsLayout.visibility = View.VISIBLE

        defectInfo.text = productDefect.comment

        updatePhotos(productDefectPhotosRecyclerView, productDefect.photos)
    }

    private fun setupPhotosRecycler(recyclerView: RecyclerView) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = ProductPhotosAdapter()
        }
    }

    private fun updatePhotos(recyclerView: RecyclerView, photos: List<ProductPhotoItem>) {
        recyclerView.isVisible = photos.isNotEmpty()
        (recyclerView.adapter as ProductPhotosAdapter).submitList(photos)
    }

    private fun setupFlagsRecycler(recyclerView: RecyclerView) {
        recyclerView.layoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
        }

        val itemDecoration = FlexboxItemDecoration(context).apply {
            setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.product_flag_spacer))
            setOrientation(FlexboxItemDecoration.HORIZONTAL)
        }
        recyclerView.addItemDecoration(itemDecoration)

        recyclerView.adapter = ProductFlagsAdapter()
    }

    private fun updateProductFlags(flags: List<ProductFlag>) {
        productFlagsRecyclerView.isVisible = flags.isNotEmpty()
        (productFlagsRecyclerView.adapter as ProductFlagsAdapter).submitList(flags)
    }
}