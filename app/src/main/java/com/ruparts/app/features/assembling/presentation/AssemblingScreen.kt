package com.ruparts.app.features.assembling.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruparts.app.R
import com.ruparts.app.features.cart.model.CartListItem

@Composable
fun AssemblingScreen(items: List<CartListItem>) {
    Scaffold(
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
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            val tabItems = listOf("Список", "Корзина")
            var selectedIndex by remember { mutableStateOf(0) }

            TabRow(
                selectedTabIndex = selectedIndex,
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier
                            .tabIndicatorOffset(tabPositions[selectedIndex])
                            .height(3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .padding(horizontal = 67.dp)
                    )
                }
            ) {
                tabItems.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                    )
                }
            }
            when (selectedIndex) {
                0 -> ListContent(items)
                // 1 -> CartContent()
            }
        }
    }
}

@Composable
private fun ListContent(items: List<CartListItem>) { //тип данных?

    Box(
        Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (items.isEmpty()) {
            Text(
                text = "Список пуст",
                color = MaterialTheme.colorScheme.secondary
            )
        } else {

            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.location2),
                            contentDescription = "",
                        )
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = "L2-A01-1-6-1",
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    }
                    Row {
                        Text(
                            text = "18/128",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .border(1.dp, SolidColor(Color.Black), RoundedCornerShape(percent = 20))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                        Icon(
                            modifier = Modifier.padding(start = 20.dp),
                            painter = painterResource(id = R.drawable.more_vert),
                            contentDescription = "",
                        )
                    }
                }

                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = "1234567890123456789012345",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    fontSize = 16.sp
                )
                Text(
                    text = "GENERAL MOTORS",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "Замок зажигания очень длинное описание очень длинное описание очень длинное описание",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp,
                )

                // for (... in ...) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .height(48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
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

                                    append("TE230101T123456XJL".substring(0, "TE230101T123456XJL".length - 3))

                                    withStyle(
                                        style = SpanStyle(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    ) {
                                        append("TE230101T123456XJL".substring("TE230101T123456XJL".length - 3))
                                    }
                                },
                                fontSize = 14.sp,
                                maxLines = 1,
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            textAlign = TextAlign.Center,
                            text = "133",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .border(1.dp, SolidColor(Color.Black), RoundedCornerShape(percent = 20))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    IconButton(
                        onClick = {},
                        Modifier.background(
                            shape = RoundedCornerShape(CornerSize(40.dp)),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                            .size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.not_found),
                            contentDescription = "",
                        )
                    }                }

                Spacer(modifier = Modifier.height(8.dp))

            }
        }
    }
}

@Preview
@Composable
private fun AssemblingScreenPreview() {
    AssemblingScreen(
        items = listOf(
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
                info = "L2-A02-1-6-1",
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
    )
}