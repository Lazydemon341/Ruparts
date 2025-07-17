package com.ruparts.app.features.qrscan.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ruparts.app.R
import com.ruparts.app.core.ui.theme.RupartsTheme
import com.ruparts.app.features.cart.model.CartListItem

class CartTransferToLocationFragment : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                RupartsTheme {
                    CartTransferModalBottomSheetScreen(
                        item = CartListItem(
                            id = 0,
                            article = "11115555669987452131",
                            brand = "Toyota",
                            quantity = 13481,
                            description = "Описание",
                            barcode = "",
                            cartOwner = "",
                        )
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CartTransferModalBottomSheetScreen(item: CartListItem) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            LazyColumn {
                stickyHeader(
                    contentType = "listHeader",
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 4.dp),
                        color = colorResource(R.color.neutral60),
                        text = "Отсканируйте один или несколько товаров,\n" +
                                "а затем ячейку или отгрузочное место",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            ModalBottomSheet(
                onDismissRequest = {
                    dismiss()
                },
                modifier = Modifier.padding(top = 16.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                dragHandle = {},
            ) {
                Column(
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.surface)
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.article,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = TextStyle(fontWeight = FontWeight.Bold),
                            fontSize = 22.sp
                        )
                        Text(
                            text = item.quantity.toString(),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .border(1.dp, SolidColor(Color.Black), RoundedCornerShape(percent = 20))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    Text(
                        text = item.brand,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = item.description,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

        }
    }

    @Preview
    @Composable
    private fun QrScanScreenPreview() {
        CartTransferModalBottomSheetScreen(
            item = CartListItem(
                id = 0,
                article = "11115555669987452131",
                brand = "Toyota",
                quantity = 13481,
                description = "Описание",
                barcode = "",
                cartOwner = "",
            )
        )
    }
}


