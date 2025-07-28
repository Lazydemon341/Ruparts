package com.ruparts.app.features.search.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruparts.app.R
import com.ruparts.app.core.ui.components.RupartsCartItem
import com.ruparts.app.features.cart.model.CartListItem

private const val listItemContentType = "listItem"

@Composable
fun SearchScreen(
    searchItems: List<CartListItem>,
) {

    var enabled by remember { mutableStateOf(true) }
    if (!enabled) return

    val scrollState = rememberScrollState()

    Column {

            Column(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
            ) {

                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .background(color = MaterialTheme.colorScheme.surfaceContainer)
                        .horizontalScroll(scrollState)
                        .wrapContentHeight(),
                ) {

                    FilterChip(
                        modifier = Modifier
                            .border(width = 1.dp, color = Color(0xFFCAC4D0), shape = RoundedCornerShape(8.dp))
                            .wrapContentHeight()
                            .height(32.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            selectedContainerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        onClick = { },
                        label = { Text("Признаки") },
                        selected = enabled,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.trailing_icon),
                                contentDescription = "",
                            )
                        },
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    FilterChip(
                        modifier = Modifier
                            .border(width = 1.dp, color = Color(0xFFCAC4D0), shape = RoundedCornerShape(8.dp))
                            .wrapContentHeight()
                            .height(32.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            selectedContainerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        onClick = { },
                        label = { Text("Расположение") },
                        selected = enabled,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.trailing_icon),
                                contentDescription = "",
                            )
                        },
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    FilterChip(
                        modifier = Modifier
                            .border(width = 1.dp, color = Color(0xFFCAC4D0), shape = RoundedCornerShape(8.dp))
                            .wrapContentHeight()
                            .height(32.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            selectedContainerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        onClick = { },
                        label = { Text("Выборки") },
                        selected = enabled,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.trailing_icon),
                                contentDescription = "",
                            )
                        },
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .background(color = MaterialTheme.colorScheme.surfaceContainer),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon (
                        painter = painterResource(id = R.drawable.downward),
                        contentDescription = "",
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


        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surfaceContainer),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                itemsIndexed(
                    items = searchItems,
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
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
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
    }
}


@Preview
@Composable
private fun SearchScreenPreview() {
    SearchScreen(
        searchItems = listOf(
            CartListItem(
                id = 1,
                article = "123457879654531",
                brand = "Toyota",
                quantity = 125,
                description = "Описание",
                barcode = "hhjruhturt",
                cartOwner = "Petrov",
                info = "",
                fromExternalInput = false
            ),
            CartListItem(
                id = 2,
                article = "987654321",
                brand = "Honda",
                quantity = 50,
                description = "Другое описание",
                barcode = "barcode-here",
                cartOwner = "Ivanov",
                info = "",
                fromExternalInput = false
            )
        ),
    )
}