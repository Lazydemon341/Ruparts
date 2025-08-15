package com.ruparts.app.core.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruparts.app.R
import com.ruparts.app.core.ui.theme.RupartsTheme
import com.ruparts.app.features.cart.model.CartListItem
import com.ruparts.app.features.cart.model.CartOwner
import com.ruparts.app.features.cart.model.OwnerType
import com.ruparts.app.features.commonlibrary.ProductFlag
import com.ruparts.app.features.commonlibrary.presentation.getIconRes

@Composable
fun RupartsCartItem(
    item: CartListItem,
    modifier: Modifier = Modifier,
    onClick: (CartListItem) -> Unit = {},
    onRemove: (CartListItem) -> Unit = {},
    enableSwipeToDismiss: Boolean = false,
    isRowVisible: Boolean = false,
    showFlags: Boolean = false,
    selectionState: RupartsCartItemSelectionState = RupartsCartItemSelectionState.NONE,
) {
    val screenWidth = LocalWindowInfo.current.containerSize.width
    val density = LocalDensity.current
    val swipeToDismissBoxState = remember {
        SwipeToDismissBoxState(
            initialValue = SwipeToDismissBoxValue.Settled,
            density = density,
            confirmValueChange = { value ->
                value != SwipeToDismissBoxValue.StartToEnd
            },
            positionalThreshold = { screenWidth / 3f },
        )
    }

    val onRemove by rememberUpdatedState(onRemove)
    LaunchedEffect(swipeToDismissBoxState.currentValue) {
        if (swipeToDismissBoxState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onRemove(item)
        }
    }

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        modifier = modifier.fillMaxSize(),
        enableDismissFromStartToEnd = false,
        gesturesEnabled = enableSwipeToDismiss,
        backgroundContent = {
            if (swipeToDismissBoxState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                Icon(
                    painter = painterResource(id = R.drawable.delete_white),
                    contentDescription = "Remove item",
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFB3261E))
                        .wrapContentSize(Alignment.CenterEnd)
                        .padding(12.dp),
                    tint = Color.White
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = if (selectionState == RupartsCartItemSelectionState.SELECTED) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                )
                .clickable { onClick(item) }
                .padding(vertical = 12.dp, horizontal = 16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.article,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = item.quantity.toString(),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .border(1.dp, SolidColor(Color.Black), RoundedCornerShape(percent = 20))
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                SelectionItem(selectionState = selectionState)
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
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (isRowVisible) {
                Row {
                    Row(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .wrapContentWidth()
                            .background(
                                shape = RoundedCornerShape(CornerSize(5.dp)),
                                color = Color(0xFFFFE8A3)
                            )
                            .padding(horizontal = 4.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.scanner),
                            contentDescription = "",
                        )
                        Text(
                            text = buildAnnotatedString {
                                append(item.barcode.substring(0, item.barcode.length - 3))

                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                                    append(item.barcode.substring(item.barcode.length - 3))
                                }
                            },
                            maxLines = 1,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(top = 8.dp, start = 4.dp)
                            .wrapContentWidth()
                            .background(
                                shape = RoundedCornerShape(CornerSize(5.dp)),
                                color = Color(0xFFE8DEF8)
                            )
                            .padding(horizontal = 4.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        when (item.cartOwner.type) {
                            OwnerType.Location -> {
                                Icon(
                                    modifier = Modifier.size(18.dp),
                                    imageVector = Icons.Outlined.LocationOn,
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                )
                            }

                            OwnerType.Cart -> {
                                Icon(
                                    modifier = Modifier.size(18.dp),
                                    painter = painterResource(id = R.drawable.cart2),
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                )
                            }
                        }
                        Text(
                            text = item.cartOwner.text,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            if (showFlags) {
                LazyRow(
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    items(
                        items = item.flags,
                        key = { it.id },
                    ) { flag ->
                        flag.getIconRes()?.let { iconRes ->
                            Icon(
                                painter = painterResource(iconRes),
                                contentDescription = "",
                                modifier = Modifier.size(26.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectionItem(selectionState: RupartsCartItemSelectionState) {
    AnimatedContent(selectionState) { targetSelectionState ->
        if (targetSelectionState != RupartsCartItemSelectionState.NONE) {
            RadioButton(
                modifier = Modifier
                    .size(40.dp)
                    .padding(8.dp),
                selected = targetSelectionState == RupartsCartItemSelectionState.SELECTED,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(40.dp)
                    .padding(8.dp)
            )
        }
    }
}

enum class RupartsCartItemSelectionState {
    SELECTED,
    NOT_SELECTED,
    NONE,
}

@Preview(name = "Default state with flags")
@Composable
private fun RupartsCartItemPreview() {
    val mockItem = CartListItem(
        id = 1,
        article = "123456789",
        brand = "Toyota",
        quantity = 125,
        description = "Замок зажигания длинное описание детали которое может не поместиться",
        barcode = "TE250630T235959II2",
        cartOwner = CartOwner(type = OwnerType.Cart, text = "Petrov"),
        info = "L2-A02-1-6-1",
        flags = listOf(
            ProductFlag(1, "Требуется измерить", "measure"),
            ProductFlag(2, "Требуется взвесить", "weight"),
            ProductFlag(3, "Хрупкий", "fragile")
        ),
        fromExternalInput = false
    )

    RupartsTheme {
        RupartsCartItem(
            item = mockItem,
            isRowVisible = true,
            showFlags = true
        )
    }
}

@Preview(name = "Selection mode - selected")
@Composable
private fun RupartsCartItemSelectedPreview() {
    val mockItem = CartListItem(
        id = 2,
        article = "987654321",
        brand = "Honda",
        quantity = 50,
        description = "Фильтр воздушный",
        barcode = "TE250630T235959II3",
        cartOwner = CartOwner(type = OwnerType.Location, text = "L1-B03-2-4-2"),
        info = "L1-B03-2-4-2",
        flags = emptyList(),
        fromExternalInput = false
    )

    RupartsTheme {
        RupartsCartItem(
            item = mockItem,
            isRowVisible = true,
            showFlags = true,
            selectionState = RupartsCartItemSelectionState.SELECTED
        )
    }
}

@Preview(name = "Selection mode - not selected")
@Composable
private fun RupartsCartItemNotSelectedPreview() {
    val mockItem = CartListItem(
        id = 3,
        article = "456789012",
        brand = "Nissan",
        quantity = 200,
        description = "Тормозные колодки",
        barcode = "TE250630T235959II4",
        cartOwner = CartOwner(type = OwnerType.Cart, text = "Sidorov"),
        info = "L3-C01-1-2-3",
        flags = listOf(ProductFlag(4, "Риск подделки", "fake")),
        fromExternalInput = false
    )

    RupartsTheme {
        RupartsCartItem(
            item = mockItem,
            isRowVisible = true,
            showFlags = true,
            selectionState = RupartsCartItemSelectionState.NOT_SELECTED
        )
    }
}

@Preview(name = "Minimal - no row, no flags")
@Composable
private fun RupartsCartItemMinimalPreview() {
    val mockItem = CartListItem(
        id = 4,
        article = "111222333",
        brand = "Mercedes",
        quantity = 75,
        description = "Масляный фильтр",
        barcode = "TE250630T235959II5",
        cartOwner = CartOwner(type = OwnerType.Cart, text = "Ivanov"),
        info = "L4-D01-3-5-2",
        flags = emptyList(),
        fromExternalInput = false
    )

    RupartsTheme {
        RupartsCartItem(
            item = mockItem,
            isRowVisible = false,
            showFlags = false
        )
    }
}

@Preview(name = "Long article and brand names")
@Composable
private fun RupartsCartItemLongTextPreview() {
    val mockItem = CartListItem(
        id = 5,
        article = "1234567890123456789",
        brand = "Volkswagen Group Audi",
        quantity = 9999,
        description = "Датчик положения коленчатого вала с кабелем и разъемом для автомобилей с автоматической трансмиссией",
        barcode = "TE250630T235959II6",
        cartOwner = CartOwner(type = OwnerType.Location, text = "L5-E99-99-99-99"),
        info = "L5-E99-99-99-99",
        flags = listOf(
            ProductFlag(5, "Габаритный", "large"),
            ProductFlag(6, "Без документов", "no_docs"),
            ProductFlag(7, "Неликвид", "illiquid"),
            ProductFlag(8, "Продажа в розницу", "retail")
        ),
        fromExternalInput = true
    )

    RupartsTheme {
        RupartsCartItem(
            item = mockItem,
            isRowVisible = true,
            showFlags = true,
            enableSwipeToDismiss = true
        )
    }
}

@Preview(name = "External input item")
@Composable
private fun RupartsCartItemExternalInputPreview() {
    val mockItem = CartListItem(
        id = 6,
        article = "EXT123456",
        brand = "BOSCH",
        quantity = 1,
        description = "Свеча зажигания",
        barcode = "TE250630T235959II7",
        cartOwner = CartOwner(type = OwnerType.Cart, text = "Scanner001"),
        info = "External",
        flags = emptyList(),
        fromExternalInput = true
    )

    RupartsTheme {
        RupartsCartItem(
            item = mockItem,
            isRowVisible = true,
            showFlags = false
        )
    }
}