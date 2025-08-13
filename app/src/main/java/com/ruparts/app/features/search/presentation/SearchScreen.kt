package com.ruparts.app.features.search.presentation

import android.view.KeyEvent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.ruparts.app.R
import com.ruparts.app.core.ui.components.RupartsCartItem
import com.ruparts.app.features.cart.model.CartListItem
import com.ruparts.app.features.search.presentation.model.SearchScreenEvent
import com.ruparts.app.features.search.presentation.model.SearchScreenFilter
import com.ruparts.app.features.search.presentation.model.SearchScreenFilterType
import com.ruparts.app.features.search.presentation.model.SearchScreenFlag
import com.ruparts.app.features.search.presentation.model.SearchScreenSearchSet
import com.ruparts.app.features.search.presentation.model.SearchScreenSorting
import com.ruparts.app.features.search.presentation.model.SearchScreenSortingType
import com.ruparts.app.features.search.presentation.model.SearchScreenState
import com.ruparts.app.features.search.presentation.model.SortingDirection
import kotlinx.coroutines.flow.flowOf

@Composable
fun SearchScreen(
    state: SearchScreenState,
    pagedItems: LazyPagingItems<CartListItem>,
    onEvent: (SearchScreenEvent) -> Unit,
    onKeyEvent: (KeyEvent) -> Boolean,
) {
    when (state) {
        is SearchScreenState.Loading -> {
            SearchScreenLoading()
        }

        is SearchScreenState.Error -> {
            // TODO
            Box {}
        }

        is SearchScreenState.Content -> {
            SearchScreenContent(
                state = state,
                pagedItems = pagedItems,
                onEvent = onEvent,
                onKeyEvent = onKeyEvent,
            )
        }
    }
}

@Composable
private fun SearchScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun SearchScreenContent(
    state: SearchScreenState.Content,
    pagedItems: LazyPagingItems<CartListItem>,
    onEvent: (SearchScreenEvent) -> Unit,
    onKeyEvent: (KeyEvent) -> Boolean,
) {
    val scrollState = rememberScrollState()
    Scaffold(
        modifier = Modifier.onKeyEvent {
            onKeyEvent(it.nativeKeyEvent)
        },
        contentWindowInsets = WindowInsets.systemBars.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
        ),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(SearchScreenEvent.OnScanButtonClick) },
                modifier = Modifier.padding(bottom = 8.dp, end = 4.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.scanner),
                    contentDescription = "",
                )
            }
        },
        bottomBar = {
            SearchScreenAssemblyButton(
                onAssemblyClick = { onEvent(SearchScreenEvent.OnAssemblyClick) },
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) { paddingValues ->
        val showFilterDialogFor = remember { mutableStateOf<SearchScreenFilterType?>(null) }
        val showSortingDialog = remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            SearchScreenFilters(
                filters = state.filters,
                scrollState = scrollState,
                onFilterClick = { filter ->
                    showFilterDialogFor.value = filter.type
                },
                onClearFilter = { filter ->
                    onEvent(SearchScreenEvent.ClearFilter(filter))
                },
            )
            SearchScreenSorting(
                selectedSorting = state.selectedSorting,
                onClick = { showSortingDialog.value = true }
            )
            SearchScreenItems(
                pagedItems = pagedItems,
                onItemClick = { item -> onEvent(SearchScreenEvent.OnItemClick(item)) },
            )
            showFilterDialogFor.value?.let { filterType ->
                SearchScreenFilterDialog(
                    state = state,
                    filterType = filterType,
                    onDismiss = { showFilterDialogFor.value = null },
                    onSubmitFlags = { flags ->
                        onEvent(SearchScreenEvent.FilterByFlags(flags))
                    },
                    onSubmitSearchSets = { searchSets ->
                        onEvent(SearchScreenEvent.FilterBySearchSets(searchSets))
                    },
                    onLocationFilter = { location ->
                        onEvent(SearchScreenEvent.FilterByLocation(location))
                    },
                    onScanClick = { onEvent(SearchScreenEvent.OnLocationScanClick) },
                    onSearchSetsTextChange = { text ->
                        onEvent(SearchScreenEvent.UpdateSearchSetsText(text))
                    },
                )
            }
            if (showSortingDialog.value) {
                SearchScreenSortingModalBottomSheet(
                    selectedSorting = state.selectedSorting,
                    onDismiss = { showSortingDialog.value = false },
                    onSortingSelect = { type, direction ->
                        onEvent(SearchScreenEvent.SetSorting(type, direction))
                        showSortingDialog.value = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SearchScreenFilters(
    filters: List<SearchScreenFilter>,
    onFilterClick: (SearchScreenFilter) -> Unit,
    onClearFilter: (SearchScreenFilter) -> Unit,
    scrollState: ScrollState = rememberScrollState()
) {
    Row(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .horizontalScroll(scrollState)
            .wrapContentHeight(),
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        filters.forEachIndexed { index, filter ->
            SearchScreenFilter(
                filter = filter,
                onClick = { onFilterClick(filter) },
                onClear = { onClearFilter(filter) },
            )
            if (index == filters.lastIndex) {
                Spacer(modifier = Modifier.width(16.dp))
            } else {
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }
}

@Composable
private fun SearchScreenFilter(
    filter: SearchScreenFilter,
    onClick: () -> Unit,
    onClear: () -> Unit,
) {
    FilterChip(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .wrapContentHeight()
            .height(32.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        onClick = onClick,
        label = {
            Text(
                text = when (filter.type) {
                    SearchScreenFilterType.FLAGS -> "Признаки"
                    SearchScreenFilterType.LOCATION -> "Расположение"
                    SearchScreenFilterType.SELECTIONS -> "Выборки"
                }
            )
        },
        selected = filter.selected,
        trailingIcon = {
            if (filter.selected) {
                Icon(
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onClear() },
                    imageVector = Icons.Default.Close,
                    contentDescription = "",
                )
            } else {
                Icon(
                    modifier = Modifier.size(18.dp),
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "",
                )
            }
        },
    )
}

@Composable
private fun SearchScreenSorting(
    selectedSorting: SearchScreenSorting,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = if (selectedSorting.direction == SortingDirection.DESCENDING) {
                Icons.Default.ArrowDownward
            } else {
                Icons.Default.ArrowUpward
            },
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            textAlign = TextAlign.Center,
            text = when (selectedSorting.type) {
                SearchScreenSortingType.VENDOR_CODE -> "По артикулу"
                SearchScreenSortingType.QUANTITY -> "По количеству"
                SearchScreenSortingType.LOCATION -> "По расположению"
                SearchScreenSortingType.BRAND -> "По бренду"
            },
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

private const val listItemContentType = "listItem"

@Composable
private fun SearchScreenItems(
    pagedItems: LazyPagingItems<CartListItem>,
    onItemClick: (CartListItem) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(
                count = pagedItems.itemCount,
                key = pagedItems.itemKey { it.id },
                contentType = pagedItems.itemContentType { listItemContentType }
            ) { index ->
                val item = pagedItems[index]
                if (item != null) {
                    RupartsCartItem(
                        item = item,
                        isRowVisible = true,
                        onClick = onItemClick,
                        showFlags = true,
                        modifier = Modifier.animateItem(),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (pagedItems.loadState.append == LoadState.Loading) {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchScreenAssemblyButton(onAssemblyClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
    ) {
        Button(
            onClick = onAssemblyClick,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 8.dp, bottom = 24.dp)
                .navigationBarsPadding()
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Text(
                text = "Сборка",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun SearchScreenFilterDialog(
    state: SearchScreenState.Content,
    filterType: SearchScreenFilterType,
    onDismiss: () -> Unit,
    onSubmitFlags: (Set<Long>) -> Unit,
    onSubmitSearchSets: (Set<Long>) -> Unit,
    onLocationFilter: (String) -> Unit,
    onScanClick: () -> Unit,
    onSearchSetsTextChange: (String) -> Unit,
) {
    when (filterType) {
        SearchScreenFilterType.FLAGS -> {
            SearchScreenFlagsModalBottomSheet(
                state = state,
                onDismiss = onDismiss,
                onSubmitFlags = onSubmitFlags,
            )
        }

        SearchScreenFilterType.LOCATION -> {
            LocationFilterDialog(
                onDismiss = onDismiss,
                onConfirmInput = { locationText ->
                    onLocationFilter(locationText)
                    onDismiss()
                },
                onScanClick = onScanClick,
                initialText = state.locationFilter,
            )
        }

        SearchScreenFilterType.SELECTIONS -> {
            SearchScreenSelectionsModalBottomSheet(
                state = state,
                onDismiss = onDismiss,
                onSubmitSearchSets = onSubmitSearchSets,
                onSearchSetsTextChange = onSearchSetsTextChange,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreenFlagsModalBottomSheet(
    state: SearchScreenState.Content,
    onDismiss: () -> Unit,
    onSubmitFlags: (Set<Long>) -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState(true)
    var checkedFlags by remember(state.flags) {
        mutableStateOf<Set<Long>>(
            state.flags
                .filter { it.checked }
                .mapTo(mutableSetOf()) { it.flag.id }
        )
    }
    val showClearButton by remember {
        derivedStateOf {
            checkedFlags.isNotEmpty()
        }
    }

    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = { WindowInsets(0.dp) },
    ) {
        Box {
            LazyColumn(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(bottom = 80.dp)
                    .navigationBarsPadding(),
            ) {
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 12.dp)
                            .padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        text = "Признаки",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Left
                    )
                }
                items(
                    items = state.flags,
                    key = { it.flag.id }
                ) { item ->
                    val id = item.flag.id
                    val onFlagClick: () -> Unit = {
                        checkedFlags = checkedFlags.toMutableSet().apply {
                            if (id in this) {
                                remove(id)
                            } else {
                                add(id)
                            }
                        }
                    }
                    val itemChecked by remember { derivedStateOf { id in checkedFlags } }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onFlagClick() }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(text = item.flag.title)
                        Spacer(modifier = Modifier.weight(1f))
                        Checkbox(
                            checked = itemChecked,
                            onCheckedChange = { onFlagClick() },
                        )
                    }
                }
            }
            BottomButtons(
                showClearButton = showClearButton,
                onClear = { checkedFlags = emptySet() },
                onSubmit = {
                    onSubmitFlags(checkedFlags)
                    onDismiss()
                },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreenSelectionsModalBottomSheet(
    state: SearchScreenState.Content,
    onDismiss: () -> Unit,
    onSubmitSearchSets: (Set<Long>) -> Unit,
    onSearchSetsTextChange: (String) -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState(true)
    var checkedSearchSets by remember(state.searchSets) {
        val initialValue = state.searchSets
            .filter { it.checked }
            .mapTo(mutableSetOf()) { it.id }
        mutableStateOf<Set<Long>>(initialValue)
    }
    val showClearButton by remember {
        derivedStateOf {
            checkedSearchSets.isNotEmpty()
        }
    }

    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = { WindowInsets(0.dp) }
    ) {
        Box {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(bottom = 100.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 12.dp)
                        .padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    text = "Выборки",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Left
                )
                OutlinedTextField(
                    value = state.searchSetsText,
                    onValueChange = onSearchSetsTextChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    label = { Text("Поиск по названию, id, автору") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
                LazyColumn {
                    items(
                        items = state.searchSets,
                        key = { it.id }
                    ) { item ->
                        val id = item.id
                        val onSearchSetClick: () -> Unit = {
                            checkedSearchSets = checkedSearchSets.toMutableSet().apply {
                                if (id in this) {
                                    remove(id)
                                } else {
                                    add(id)
                                }
                            }
                        }
                        val itemChecked by remember { derivedStateOf { id in checkedSearchSets } }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSearchSetClick() }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                        ) {
                            Checkbox(
                                checked = itemChecked,
                                onCheckedChange = { onSearchSetClick() }
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = item.text,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = item.supportingText,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
            BottomButtons(
                showClearButton = showClearButton,
                onClear = { checkedSearchSets = emptySet() },
                onSubmit = {
                    onSubmitSearchSets(checkedSearchSets)
                    onDismiss()
                },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun BottomButtons(
    showClearButton: Boolean,
    onClear: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
        ) {
            if (showClearButton) {
                Button(
                    onClick = onClear,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 20.dp)
                        .padding(top = 8.dp, bottom = 24.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = "Очистить",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 8.dp, bottom = 24.dp)
                    .weight(1f)
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Применить",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreenSortingModalBottomSheet(
    selectedSorting: SearchScreenSorting,
    onDismiss: () -> Unit,
    onSortingSelect: (SearchScreenSortingType, SortingDirection) -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState(true)

    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .width(32.dp)
                    .height(4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(100.dp)
                    )
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Сортировка",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            SearchScreenSortingType.entries.forEach { sortingType ->
                val isSelected = selectedSorting.type == sortingType
                val currentDirection = if (isSelected) selectedSorting.direction else SortingDirection.DESCENDING

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clickable {
                            val newDirection = if (isSelected) {
                                if (selectedSorting.direction == SortingDirection.DESCENDING) {
                                    SortingDirection.ASCENDING
                                } else {
                                    SortingDirection.DESCENDING
                                }
                            } else {
                                SortingDirection.DESCENDING
                            }
                            onSortingSelect(sortingType, newDirection)
                        },
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.surfaceContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                    shape = if (isSelected) RoundedCornerShape(100.dp) else RoundedCornerShape(0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isSelected) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = if (currentDirection == SortingDirection.ASCENDING) {
                                    Icons.Default.ArrowUpward
                                } else {
                                    Icons.Default.ArrowDownward
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        } else {
                            Spacer(modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = when (sortingType) {
                                SearchScreenSortingType.VENDOR_CODE -> "По артикулу"
                                SearchScreenSortingType.QUANTITY -> "По количеству"
                                SearchScreenSortingType.LOCATION -> "По расположению"
                                SearchScreenSortingType.BRAND -> "По бренду"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LocationFilterDialog(
    onDismiss: () -> Unit,
    onConfirmInput: (text: String) -> Unit,
    onScanClick: () -> Unit,
    initialText: String = "",
) {
    var inputText by remember(initialText) { mutableStateOf(initialText) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Расположение",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            val focusRequester = remember { FocusRequester() }

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                label = { Text("") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = false,
                    capitalization = KeyboardCapitalization.Characters,
                ),
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.scanner),
                        contentDescription = "Сканировать",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onScanClick() }
                    )
                }
            )
        },
        confirmButton = {
            TextButton(
                enabled = inputText.isNotBlank(),
                onClick = {
                    onConfirmInput(inputText)
                    onDismiss()
                }
            ) {
                Text("Применить")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("Отмена")
            }
        }
    )
}

@Preview
@Composable
private fun SearchScreenPreview() {
    val mockItems = listOf(
        CartListItem(
            id = 1,
            article = "123457879654531",
            brand = "Toyota",
            quantity = 125,
            description = "Замок зажигания",
            barcode = "TE250630T235959II2",
            cartOwner = "Petrov",
            info = "L2-A02-1-6-1",
            flags = listOf(),
            fromExternalInput = false
        ),
        CartListItem(
            id = 2,
            article = "987654321",
            brand = "Honda",
            quantity = 50,
            description = "Фильтр воздушный",
            barcode = "TE250630T235959II3",
            cartOwner = "Ivanov",
            info = "L1-B03-2-4-2",
            flags = listOf(),
            fromExternalInput = false
        ),
        CartListItem(
            id = 3,
            article = "456789012",
            brand = "Nissan",
            quantity = 200,
            description = "Тормозные колодки",
            barcode = "TE250630T235959II4",
            cartOwner = "Sidorov",
            info = "L3-C01-1-2-3",
            flags = listOf(),
            fromExternalInput = false
        )
    )
    val mockPagingData = flowOf(PagingData.from(mockItems)).collectAsLazyPagingItems()

    SearchScreen(
        state = SearchScreenState.Content(
            filters = listOf(
                SearchScreenFilter(SearchScreenFilterType.FLAGS, false),
                SearchScreenFilter(SearchScreenFilterType.LOCATION, true),
                SearchScreenFilter(SearchScreenFilterType.SELECTIONS, false)
            ),
            flags = listOf(
                SearchScreenFlag("Требуется измерить", false),
                SearchScreenFlag("Требуется взвесить", false),
                SearchScreenFlag("Требуется фото", false),
                SearchScreenFlag("Риск подделки", true),
                SearchScreenFlag("Продажа в розницу", false),
                SearchScreenFlag("Неликвид", false),
                SearchScreenFlag("Габаритный", false),
                SearchScreenFlag("Хрупкий", true),
                SearchScreenFlag("Загружен с Фрозы", false),
                SearchScreenFlag("Без документов", false),
            ),
            searchSets = listOf(
                SearchScreenSearchSet("Для бухгалтерии", "ID 3, Петров Н.А., 14.06.2025", false),
                SearchScreenSearchSet("Срочно", "ID 69, Иванов В.М., 28.05.2025", false),
                SearchScreenSearchSet("Для доставки", "ID 25, Сидоров Г.Д., 21.05.2025", false),
                SearchScreenSearchSet("Для менеджера", "ID 73, Судоровозражанов Н.А., 14.06.2025", false),
            ),
            selectedSorting = SearchScreenSorting(),
            locationFilter = "",
            searchSetsText = "",
        ),
        pagedItems = mockPagingData,
        onEvent = {},
        onKeyEvent = { true },
    )
}