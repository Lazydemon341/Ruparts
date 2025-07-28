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
import com.ruparts.app.databinding.ProductUnitBinding
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

    private var _binding: ProductUnitBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProductUnitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.paddingAboveSystemBars()

        setupMenu()

        // Setup photo recycler views
        setupPhotosRecycler(binding.productPhotos)
        setupPhotosRecycler(binding.productCardPhotos)
        setupPhotosRecycler(binding.productDefectPhotos)

        // Setup flag recycler views
        setupFlagsRecycler(binding.productFlagsList)
        setupFlagsRecycler(binding.productInfoFlagsList)

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
                binding.loadingIndicator.isVisible = false
                binding.productContent.isVisible = true

                binding.articleValue.text = state.product.vendorCode
                binding.brandValue.text = state.product.brand
                binding.quantityValue.text = state.product.quantity.toString()
                binding.descriptionValue.text = state.product.description
                binding.barcodeValue.text = state.product.barcode
                binding.addressValue.text = state.product.location
                binding.dateValue.text = state.product.acceptedAt.formatSafely(dateFormatter)

                val hasComment = !state.product.unitComment.isNullOrBlank()
                binding.commentName.isVisible = hasComment
                binding.commentValue.isVisible = hasComment
                binding.commentValue.text = state.product.unitComment

                updatePhotos(binding.productPhotos, state.product.photos)
                updateProductFlags(binding.productFlagsList, binding.productFlags, state.product.flags)

                updateProductCard(state.product.card)
                updateProductDefects(state.product.defect)
            }

            ProductDetailsScreenState.Loading -> {
                binding.loadingIndicator.isVisible = true
                binding.productContent.isVisible = false
            }
        }
    }

    private fun updateProductCard(productCard: ProductCard?) {
        if (productCard == null) {
            binding.productCard.visibility = View.GONE
            return
        }
        binding.productCard.visibility = View.VISIBLE

        binding.weightValue.text = productCard.weight?.let { "$it кг" } ?: "-"
        binding.heightValue.text = productCard.sizeHeight?.let { "$it мм" } ?: "-"
        binding.widthValue.text = productCard.sizeWidth?.let { "$it мм" } ?: "-"
        binding.lengthValue.text = productCard.sizeLength?.let { "$it мм" } ?: "-"

        val hasCardComment = !productCard.comment.isNullOrBlank()
        binding.commentInfoName.isVisible = hasCardComment
        binding.commentInfoValue.isVisible = hasCardComment
        binding.commentInfoValue.text = productCard.comment

        updateProductFlags(binding.productInfoFlagsList, binding.productInfoFlags, productCard.flags)
        updatePhotos(binding.productCardPhotos, productCard.photos)
    }

    private fun updateProductDefects(productDefect: ProductDefect?) {
        if (productDefect == null || (productDefect.comment.isNullOrBlank() && productDefect.photos.isEmpty())) {
            binding.productDefects.visibility = View.GONE
            return
        }
        binding.productDefects.visibility = View.VISIBLE

        binding.defectInfo.text = productDefect.comment

        updatePhotos(binding.productDefectPhotos, productDefect.photos)
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
    
    private fun updateProductFlags(recyclerView: RecyclerView, titleView: TextView, flags: List<ProductFlag>) {
        val hasFlags = flags.isNotEmpty()
        recyclerView.isVisible = hasFlags
        titleView.isVisible = hasFlags
        (recyclerView.adapter as ProductFlagsAdapter).submitList(flags)
    }
}