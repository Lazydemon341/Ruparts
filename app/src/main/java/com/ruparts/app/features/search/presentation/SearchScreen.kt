package com.ruparts.app.features.search.presentation

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.navigation.fragment.findNavController
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.fragment.findNavController
import com.ruparts.app.R
import com.ruparts.app.core.ui.components.RupartsCartItem
import com.ruparts.app.features.cart.model.CartListItem

@Composable
fun SearchScreen(
    state: SearchScreenState
) {
    val scrollState = rememberScrollState()
    Scaffold(
        contentWindowInsets = WindowInsets.systemBars.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
        ),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
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
            SearchScreenAssemblyButton()
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) { paddingValues ->
        val showFilterDialogFor = remember { mutableStateOf<SearchScreenFilterType?>(null) }
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            SearchScreenFilters(
                filters = state.filters,
                scrollState = scrollState,
                onFilterClick = { filter ->
                    showFilterDialogFor.value = filter.type
                }
            )
            SearchScreenSorting()
            SearchScreenItems(
                items = state.items,
            )

            showFilterDialogFor.value?.let { filterType ->
                SearchScreenFilterDialog(
                    filterType = filterType,
                    onDismiss = { showFilterDialogFor.value = null },
                )
            }
        }
    }
}

@Composable
private fun SearchScreenFilters(
    filters: List<SearchScreenFilter>,
    onFilterClick: (SearchScreenFilter) -> Unit,
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
                onClick = { onFilterClick(filter) }
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
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = if (filter.selected) {
                    Icons.Default.Close
                } else {
                    Icons.Default.ArrowDropDown
                },
                contentDescription = "",
            )
        },
    )
}

@Composable
private fun SearchScreenSorting() {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .background(color = MaterialTheme.colorScheme.surfaceContainer),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = Icons.Default.ArrowDownward,
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            textAlign = TextAlign.Center,
            text = "По количеству",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

private const val listItemContentType = "listItem"

@Composable
private fun SearchScreenItems(
    items: List<CartListItem>
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(
                items = items,
                key = { _, item -> item.id },
                contentType = { _, _ -> listItemContentType },
            ) { index, item ->
                RupartsCartItem(
                    item = item,
                    onRemove = {},
                    enableSwipeToDismiss = false,
                    isRowVisible = true
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SearchScreenAssemblyButton() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
    ) {
        Button(
            onClick = { },
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 8.dp, bottom = 24.dp)
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
    filterType: SearchScreenFilterType,
    onDismiss: () -> Unit,
) {
    when (filterType) {
        SearchScreenFilterType.FLAGS -> {
            SearchScreenModalBottomSheet()
        }


        SearchScreenFilterType.LOCATION -> {

        }

        SearchScreenFilterType.SELECTIONS -> {

        }
    }
}


@Preview
@Composable
private fun SearchScreenPreview() {
    SearchScreen(
        state = SearchScreenState(
            items = emptyList(),
            filters = listOf(
                SearchScreenFilter(SearchScreenFilterType.FLAGS, false),
                SearchScreenFilter(SearchScreenFilterType.LOCATION, true),
                SearchScreenFilter(SearchScreenFilterType.SELECTIONS, false)
            )
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreenModalBottomSheet() {
    val bottomSheetState = rememberModalBottomSheetState(true)
    var checked by remember { mutableStateOf(true) }
    val items = listOf("Требуется измерить", "Требуется взвесить", "Требуется фото")
//    var checkedItems = mutableStateListOf(items)

    ModalBottomSheet(
        dragHandle = {BottomSheetDefaults.DragHandle()},
        sheetState = bottomSheetState,
        onDismissRequest = {},
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Box(contentAlignment = Alignment.BottomCenter) {
            Column {
                LazyColumn {
                    stickyHeader(contentType = "listHeader") {
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
                }
                items.forEachIndexed { index, item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(text = item)
                        Spacer(modifier = Modifier.weight(1f))
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { checked = it }
                        )
                    }
                }
                if (!checked) {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(top = 8.dp, bottom = 24.dp)
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
                } else {
                    Row(
                        Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 20.dp)
                                .padding(top = 8.dp, bottom = 24.dp)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            )
                        ) {
                            Text(
                                text = "Очистить",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp, end = 20.dp)
                                .padding(top = 8.dp, bottom = 24.dp)
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
        }
    }
}




