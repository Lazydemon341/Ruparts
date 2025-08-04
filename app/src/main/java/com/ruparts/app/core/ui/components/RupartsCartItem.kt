package com.ruparts.app.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruparts.app.R
import com.ruparts.app.features.cart.model.CartListItem
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
                .background(color = MaterialTheme.colorScheme.surface)
                .clickable { onClick(item) }
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
                            .padding(horizontal = 4.dp, vertical = 2.dp),
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
                            fontSize = 14.sp,
                            maxLines = 1,
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
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.cart2),
                            contentDescription = "",
                        )
                        Text(
                            text = item.cartOwner.substring(1),
                            fontSize = 14.sp,
                            maxLines = 1,
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
                            Image(
                                painter = painterResource(iconRes),
                                contentDescription = "",
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}