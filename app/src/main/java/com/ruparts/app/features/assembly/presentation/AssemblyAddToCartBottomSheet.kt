package com.ruparts.app.features.assembly.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ruparts.app.R
import com.ruparts.app.features.cart.model.CartListItem
import com.ruparts.app.features.cart.model.CartOwner
import com.ruparts.app.features.cart.model.OwnerType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssemblyAddToCartBottomSheet(
    item: CartListItem,
    dismiss: () -> Unit,
    bottomSheetState: SheetState = rememberModalBottomSheetState(true)
) {
    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = dismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.outline)
        },
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            color = colorResource(R.color.neutral60),
            text = "Товар будет перемещён в корзину",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        AssemblyItemRow(item)
        Spacer(modifier = Modifier.height(8.dp))
        BottomButtons(item)
    }
}

@Composable
private fun AssemblyItemRow(
    item: CartListItem,
) {
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
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = item.quantity.toString(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .border(1.dp, SolidColor(Color.Black), RoundedCornerShape(percent = 20))
                        .padding(horizontal = 6.dp, vertical = 1.dp),
                )
            }
            Text(
                text = item.brand,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = item.description,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun BottomButtons(
    item: CartListItem,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .padding(start = 20.dp, top = 8.dp, bottom = 24.dp, end = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CancelButton(
                itemId = item.id,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    ),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.CallSplit,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    ),
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

@Composable
private fun CancelButton(
    itemId: Long,
    modifier: Modifier = Modifier,
) {
    val cancelButtonBackground = remember(itemId) {
        Animatable(0f)
    }
    LaunchedEffect(itemId) {
        cancelButtonBackground.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 10_000, easing = LinearEasing),
            block = {
                if (this.value == 1f) {
                    // TODO: Что должно происходить, когда анимация закончилась?
                    // TODO: Уточнить у Яноша
                }
            }
        )
    }
    Button(
        onClick = {},
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondaryContainer),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = modifier
            .height(56.dp),
        contentPadding = PaddingValues(0.dp),
    ) {
        Box(Modifier.fillMaxSize()) {
            val secondaryContainerColor = MaterialTheme.colorScheme.secondaryContainer
            Canvas(
                Modifier
                    .height(56.dp)
                    .fillMaxWidth()
            ) {
                drawRect(
                    color = secondaryContainerColor,
                    size = Size(size.width * cancelButtonBackground.value, size.height)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ButtonDefaults.ContentPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
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
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true)
@Composable
private fun AssemblyAddToCartBottomSheetPreview() {
    AssemblyAddToCartBottomSheet(
        item = CartListItem(
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
        ),
        dismiss = {},
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Expanded,
            skipHiddenState = true,
        )
    )
}