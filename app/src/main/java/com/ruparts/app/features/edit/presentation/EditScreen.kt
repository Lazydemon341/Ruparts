package com.ruparts.app.features.edit.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.WrongLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ruparts.app.features.assembly.presentation.model.AssemblyScreenEvent

@Composable
fun EditScreen() {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) { paddingValues ->
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                // .verticalScroll(rememberScrollState())
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            ) {
                MainBlock()
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            ) {
                CardBlock()
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            ) {
                DefectBlock()
            }
        }
    }
}

@Composable
fun MainBlock() {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
    )
    {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                modifier = Modifier.width(80.dp),
                text = "Артикул",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = "1234567890123456789012345",
                color = Color.Black,
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            Text(
                modifier = Modifier.width(80.dp),
                text = "Бренд",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = "GENERAL MOTORS",
                color = Color.Black,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        OutlinedTextField(
            value = "Замок зажигания очень длинное описание очень длинное описание очень длинное описание",
            onValueChange = {
                // newText -> text = newText
            },
            label = { Text("Описание") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        ) {
            Text(
                modifier = Modifier.width(80.dp),
                text = "Кол-во",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = "12345",
                color = Color.Black,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            Text(
                modifier = Modifier.width(80.dp),
                text = "Штрихкод",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(Modifier.width(16.dp))
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .background(
                        shape = RoundedCornerShape(CornerSize(5.dp)),
                        color = Color(0xFFFFE8A3)
                    ),
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 6.dp),
                    text = "A20250521T214852JL",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            Text(
                modifier = Modifier.width(80.dp),
                text = "Адрес",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(Modifier.width(16.dp))
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .background(
                        shape = RoundedCornerShape(CornerSize(5.dp)),
                        color = Color(0xFFE8DEF8)
                    ),
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 6.dp),
                    text = "L2-A01-11-16-12",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            Text(
                modifier = Modifier.width(80.dp),
                text = "Приход",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = "2 янв 2024 г (132 дня)",
                color = Color.Black,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        SignsBlock()
        OutlinedTextField(
            value = "Короткий комментарий к товарной единице",
            onValueChange = {
                // newText -> text = newText
            },
            label = { Text("Комментарий") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        PhotoBlock()
    }
}

@Composable
fun CardBlock() {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Карточка",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            OutlinedTextField(
                value = "12",
                onValueChange = {
                    // newText -> text = newText
                },
                label = { Text("Вес") },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(16.dp))
            OutlinedTextField(
                value = "3000",
                onValueChange = {
                    // newText -> text = newText
                },
                label = { Text("Ширина") },
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            OutlinedTextField(
                value = "120",
                onValueChange = {
                    // newText -> text = newText
                },
                label = { Text("Длина") },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(16.dp))
            OutlinedTextField(
                value = "28",
                onValueChange = {
                    // newText -> text = newText
                },
                label = { Text("Высота") },
                modifier = Modifier.weight(1f)
            )
        }
        SignsBlock()
        OutlinedTextField(
            value = "Короткий комментарий к карточке товара",
            onValueChange = {
                // newText -> text = newText
            },
            label = { Text("Комментарий") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        PhotoBlock()
    }
}

@Composable
fun SignsBlock() {
    Text(
        modifier = Modifier.padding(top = 16.dp),
        text = "Признаки",
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.bodyLarge,
    )
    FlowRow(
        modifier = Modifier.padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(10) { index ->
            Button(
                onClick = { },
                modifier = Modifier.wrapContentWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = "Признак",
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Composable
fun PhotoBlock() {
    Row(modifier = Modifier.padding(top = 16.dp)) {
        // Image(
        //     painter = Painters.empty(),
        //     contentDescription = "",
        //     modifier = Modifier.size(64.dp)
        // )
        IconButton(
            onClick = { },
            modifier = Modifier.size(40.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}

@Composable
fun DefectBlock() {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Дефект",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "",
                modifier = Modifier.size(24.dp)
            )
        }
        OutlinedTextField(
            value = "Корпус немного треснул",
            onValueChange = {
                // newText -> text = newText
            },
            label = { Text("Комментарий дефекта") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        PhotoBlock()
    }
}

@Preview
@Composable
private fun EditScreenPreview() {
    EditScreen()
}