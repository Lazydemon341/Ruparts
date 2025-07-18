package com.ruparts.app.features.cart.presentation.transfertolocation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ruparts.app.R
import com.ruparts.app.core.barcode.ExternalCodeInputHandler
import com.ruparts.app.core.ui.theme.RupartsTheme
import com.ruparts.app.core.ui.viewmodel.assistedViewModels
import com.ruparts.app.features.cart.model.CartListItem
import com.ruparts.app.features.cart.presentation.CartFragment
import com.ruparts.app.features.cart.presentation.CartFragment.Companion.CART_TOAST_TO_SHOW_KEY
import com.ruparts.app.features.cart.presentation.transfertolocation.model.CartTransferToLocationScreenEffect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay


@AndroidEntryPoint
class CartTransferToLocationFragment : DialogFragment() {

    private val args: CartTransferToLocationFragmentArgs by navArgs()
    private val viewModel by assistedViewModels<CartTransferToLocationViewModel, CartTransferToLocationViewModel.Factory> {
        create(
            scannedItem = args.scannedItem,
        )
    }

    private val externalCodeInputHandler = ExternalCodeInputHandler { code, type ->
        viewModel.onExternalCodeReceived(code, type)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                RupartsTheme {
                    val state by viewModel.state.collectAsState()
                    CartTransferModalBottomSheetScreen(
                        items = state.items
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setToolbarSubtitle()
    }

    private fun setToolbarSubtitle() {
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = if (args.cartItems.isEmpty()) {
            null
        } else {
            val itemsCount = args.cartItems.size
            resources.getQuantityString(
                R.plurals.cart_items_count,
                itemsCount,
                itemsCount,
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun CartTransferModalBottomSheetScreen(
        items: List<CartListItem>,
    ) {
        val bottomSheetState = rememberModalBottomSheetState(true)
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.effects.collect { effect ->
                when (effect) {
                    is CartTransferToLocationScreenEffect.NavigateBack -> {
                        if (effect.updateCart || !effect.toastToShow.isNullOrEmpty()) {
                            setFragmentResult(
                                CartFragment.CART_UPDATED_REQUEST_KEY,
                                bundleOf(
                                    CART_TOAST_TO_SHOW_KEY to effect.toastToShow,
                                ),
                            )
                        }
                        findNavController().popBackStack()
                    }

                    is CartTransferToLocationScreenEffect.ShowToast -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                }
            }
        }

        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(Unit) {
            delay(300)
            focusRequester.requestFocus()
        }
        ModalBottomSheet(
            sheetState = bottomSheetState,
            modifier = Modifier
                .onKeyEvent { event ->
                    if (event.key != Key.Back && event.type == KeyEventType.KeyDown) {
                        val char = event.nativeKeyEvent.unicodeChar.toChar()
                        externalCodeInputHandler.handleInput(char)
                        return@onKeyEvent true
                    }

                    return@onKeyEvent false
                }
                .focusRequester(focusRequester)
                .focusable(),
            onDismissRequest = {
                findNavController().popBackStack()
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            Box(
                contentAlignment = Alignment.BottomCenter,
            ) {
                LazyColumn {
                    stickyHeader(
                        contentType = "listHeader",
                    ) {
                        Text(
                            modifier = Modifier.Companion
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 16.dp)
                                .padding(horizontal = 16.dp),
                            color = colorResource(R.color.neutral60),
                            text = "Отсканируйте один или несколько товаров,\n" +
                                    "а затем ячейку или отгрузочное место",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Companion.Center,
                        )
                    }

                    items(
                        items = items,
                        key = { it.id },
                        contentType = { "listItem" },
                    ) { item ->
                        CartTransferToLocationItem(item)
                    }
                }
                SnackbarHost(
                    hostState = snackbarHostState,
                )
            }
        }
    }

    @Composable
    private fun CartTransferToLocationItem(
        item: CartListItem,
    ) {
        Column(
            modifier = Modifier.Companion
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(vertical = 12.dp, horizontal = 16.dp),
        ) {
            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                Text(
                    text = item.article,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = TextStyle(fontWeight = FontWeight.Companion.Bold),
                    fontSize = 22.sp
                )
                Text(
                    text = item.quantity.toString(),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    modifier = Modifier.Companion
                        .border(1.dp, SolidColor(Color.Companion.Black), RoundedCornerShape(percent = 20))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
            Text(
                text = item.brand,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                modifier = Modifier.Companion.padding(top = 4.dp)
            )
            Text(
                text = item.description,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                modifier = Modifier.Companion.padding(top = 4.dp),
                maxLines = 2,
                overflow = TextOverflow.Companion.Ellipsis
            )
        }
    }

    @Preview
    @Composable
    private fun QrScanScreenPreview() {
        CartTransferModalBottomSheetScreen(
            items = listOf(
                CartListItem(
                    id = 0,
                    article = "11115555669987452131",
                    brand = "Toyota",
                    quantity = 13481,
                    description = "Описание",
                    barcode = "",
                    cartOwner = "",
                )
            )
        )
    }
}