package com.ruparts.app.features.assembly.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruparts.app.R
import com.ruparts.app.features.cart.model.CartListItem
import com.ruparts.app.features.cart.model.CartOwner
import com.ruparts.app.features.cart.model.OwnerType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssemblyAddToCartBottomSheet(
    item: CartListItem
) {
    val bottomSheetState = rememberModalBottomSheetState(true)

    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = {
            // findNavController().popBackStack()
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        dragHandle = {},
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter,
        ) {
            Column() {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = colorResource(R.color.neutral60),
                    text = "Товар будет перемещён в корзину",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
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
                                fontSize = 16.sp
                            )
                            Text(
                                text = item.quantity.toString(),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .border(1.dp, SolidColor(Color.Black), RoundedCornerShape(percent = 20))
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            )
                        }
                        Text(
                            text = item.brand,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = item.description,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .padding(start = 20.dp, top = 8.dp, bottom = 48.dp, end = 20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        modifier = Modifier.height(56.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = "Отменить",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .size(56.dp)
                            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(16.dp))
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.split),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .size(56.dp)
                            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(16.dp))
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.alert),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun AssemblyAddToCartBottomSheetPreview() {
    AssemblyAddToCartBottomSheet(
        CartListItem(
            id = 0,
            article = "11115555669987452131",
            brand = "Toyota",
            quantity = 13481,
            description = "Описание",
            barcode = "",
            cartOwner = CartOwner(
                type = OwnerType.Location,
                text = ""
            ),
            info = "",
        )
    )
}