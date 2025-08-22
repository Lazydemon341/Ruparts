package com.ruparts.app.features.assembly.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.WrongLocation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruparts.app.R
import com.ruparts.app.core.ui.components.RupartsCartItem
import com.ruparts.app.features.assembly.presentation.model.AssemblyScreenEvent
import com.ruparts.app.features.cart.model.CartListItem
import com.ruparts.app.features.cart.model.CartOwner
import com.ruparts.app.features.cart.model.OwnerType

@Composable
fun AssemblyScreen(
    state: AssemblyScreenState,
    onEvent: (AssemblyScreenEvent) -> Unit,
) {
    when (state) {
        is AssemblyScreenState.Loading -> {
            AssemblyScreenLoading()
        }

        is AssemblyScreenState.Error -> {
            AssemblyScreenError()
        }

        is AssemblyScreenState.Content -> {
            AssemblyScreenContent(
                state = state,
                onEvent = onEvent,
            )
        }
    }
}

@Composable
private fun AssemblyScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun AssemblyScreenError() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Ошибка загрузки",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssemblyScreenContent(
    state: AssemblyScreenState.Content,
    onEvent: (AssemblyScreenEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            AssemblyTabRow(
                selectedTab = state.selectedTab,
                listCount = state.assemblyGroups.size,
                basketCount = state.basketItems.size,
                onTabClick = { tab -> onEvent(AssemblyScreenEvent.OnTabClick(tab)) }
            )
        },
        floatingActionButton = {
            if (state.selectedTab != AssemblyTab.LIST || state.assemblyGroups.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { onEvent(AssemblyScreenEvent.OnScanClick(state.selectedTab)) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.scanner),
                        contentDescription = "",
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.systemBars.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
        ),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (state.selectedTab) {
                AssemblyTab.LIST -> {
                    AssemblyList(
                        groups = state.assemblyGroups,
                        onEvent = onEvent,
                    )
                }

                AssemblyTab.BASKET -> {
                    AssemblyBasket(
                        basketItems = state.basketItems,
                    )
                }
            }
        }
    }
}

@Composable
private fun AssemblyTabRow(
    selectedTab: AssemblyTab,
    listCount: Int,
    basketCount: Int,
    onTabClick: (AssemblyTab) -> Unit,
) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.primary,
    ) {
        AssemblyTab(
            tabType = AssemblyTab.LIST,
            selected = selectedTab == AssemblyTab.LIST,
            itemsCount = listCount,
            onTabClick = onTabClick,
        )
        AssemblyTab(
            tabType = AssemblyTab.BASKET,
            selected = selectedTab == AssemblyTab.BASKET,
            itemsCount = basketCount,
            onTabClick = onTabClick,
        )
    }
}

@Composable
private fun AssemblyTab(
    tabType: AssemblyTab,
    selected: Boolean,
    itemsCount: Int,
    onTabClick: (AssemblyTab) -> Unit,
) {
    val textColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    Tab(
        selected = selected,
        onClick = { onTabClick(tabType) },
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = when (tabType) {
                        AssemblyTab.LIST -> "Список"
                        AssemblyTab.BASKET -> "Корзина"
                    },
                    color = textColor,
                    style = MaterialTheme.typography.titleSmall,
                )
                if (itemsCount > 0) {
                    TabCountIndicator(itemsCount, selected)
                }
            }
        }
    )
}

@Composable
private fun TabCountIndicator(
    count: Int,
    selected: Boolean,
) {
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }
    val textColor = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.secondary
    }
    Text(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp),
        text = count.toString(),
        color = textColor,
        style = MaterialTheme.typography.labelSmall,
        fontSize = 12.sp
    )
}

@Composable
private fun AssemblyList(
    groups: List<AssemblyGroup>,
    onEvent: (AssemblyScreenEvent) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (groups.isEmpty()) {
            Text(
                text = "Список пуст",
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(id = R.color.secondary60),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(groups) { group ->
                    AssemblyGroupCard(
                        group = group,
                        onEvent = onEvent,
                    )
                }
                // space for fab
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
private fun AssemblyGroupCard(
    group: AssemblyGroup,
    onEvent: (AssemblyScreenEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = group.locationName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                text = "${group.completedCount}/${group.totalCount}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Long identifier
        Text(
            text = "123456789012345678901234567890123456789",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "GENERAL MOTORS",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "Замок зажигания очень длинное описание оч...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Items
        group.items.forEach { item ->
            key(item.id) {
                AssemblyItemRow(
                    item = item,
                    onEvent = onEvent,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun AssemblyItemRow(
    item: AssemblyItem,
    onEvent: (AssemblyScreenEvent) -> Unit,
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

    LaunchedEffect(swipeToDismissBoxState.currentValue) {
        if (swipeToDismissBoxState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onEvent(AssemblyScreenEvent.OnDeleteClick(item))
        }
    }

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        modifier = Modifier.fillMaxWidth(),
        enableDismissFromStartToEnd = false,
        gesturesEnabled = true,
        backgroundContent = {
            if (swipeToDismissBoxState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                Icon(
                    painter = painterResource(id = R.drawable.delete_white),
                    contentDescription = "Remove item",
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.error)
                        .wrapContentSize(Alignment.CenterEnd)
                        .padding(12.dp),
                    tint = MaterialTheme.colorScheme.onError
                )
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .clickable { onEvent(AssemblyScreenEvent.OnItemClick(item)) },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f),
            ) {
                Row(
                    modifier = Modifier
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
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(5.dp),
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    text = item.quantity.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            IconButton(
                onClick = { onEvent(AssemblyScreenEvent.OnLocationClick(item)) },
                modifier = Modifier.size(40.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.WrongLocation,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
    }
}

@Composable
private fun AssemblyBasket(
    basketItems: List<CartListItem>,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (basketItems.isEmpty()) {
            Text(
                text = "Корзина пуста",
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(id = R.color.secondary60),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = basketItems,
                    key = { item -> item.id },
                ) { item ->
                    RupartsCartItem(
                        item = item,
                        onClick = {
                            // TODO: Navigate to item details
                        },
                        enableSwipeToDismiss = false,
                        isRowVisible = true,
                        showFlags = true,
                        modifier = Modifier.animateItem(),
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun AssemblyScreenPreview() {
    val mockState = AssemblyScreenState.Content(
        selectedTab = AssemblyTab.LIST,
        assemblyGroups = listOf(
            AssemblyGroup(
                locationId = "L2-A01-1-6-1",
                locationName = "L2-A01-1-6-1",
                completedCount = 18,
                totalCount = 128,
                items = listOf(
                    AssemblyItem(
                        id = 1,
                        barcode = "TE23010111123456XJL",
                        article = "123456",
                        brand = "GENERAL MOTORS",
                        description = "Замок зажигания",
                        quantity = 133,
                    ),
                    AssemblyItem(
                        id = 2,
                        barcode = "A20250521T214852WVE",
                        article = "214852",
                        brand = "GENERAL MOTORS",
                        description = "Фильтр масляный",
                        quantity = 88
                    ),
                    AssemblyItem(
                        id = 3,
                        barcode = "A20250521T214852QK5",
                        article = "214852",
                        brand = "GENERAL MOTORS",
                        description = "Прокладка головки блока",
                        quantity = 34
                    )
                )
            ),
            AssemblyGroup(
                locationId = "K1-B04-3-2-6",
                locationName = "K1-B04-3-2-6",
                completedCount = 3,
                totalCount = 45,
                items = listOf(
                    AssemblyItem(
                        id = 4,
                        barcode = "TE23010111123456XJL",
                        article = "123456",
                        brand = "GENERAL MOTORS",
                        description = "Замок зажигания очень длинное описание очень...",
                        quantity = 14
                    )
                )
            )
        ),
        basketItems = listOf(
            CartListItem(
                id = 2,
                article = "987654321",
                brand = "Honda",
                quantity = 50,
                description = "Фильтр воздушный",
                barcode = "TE250630T235959II3",
                cartOwner = CartOwner(type = OwnerType.Cart, text = "Ivanov"),
                info = "L2-A02-1-6-1",
                flags = listOf(),
                fromExternalInput = false
            ),
        ),
    )
    AssemblyScreen(
        state = mockState,
        onEvent = {},
    )
}