package com.ruparts.app.features.cartItem.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ruparts.app.R
import com.ruparts.app.core.ui.theme.RupartsTheme

class CartItemActionsFragment : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                RupartsTheme {
                    ModalBottomSheetScreen()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ModalBottomSheetScreen() {
        ModalBottomSheet(
            onDismissRequest = {
                dismiss()
            },
            modifier = Modifier.padding(top = 16.dp),
            containerColor = MaterialTheme.colorScheme.onSurface,
            dragHandle = {},
        ) {
            Column {
                Button(
                    onClick = { },
                    modifier = Modifier.padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSurface,
                        contentColor = Color(0xFF1D1B20)  )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.split),
                        contentDescription = "",
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = "Разделить",
                        fontSize = 16.sp,
                    )
                }
                Button(
                    onClick = { },
                    modifier = Modifier.padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSurface,
                        contentColor = Color(0xFF1D1B20)  )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.alert),
                        contentDescription = "",
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = "Дефект",
                        fontSize = 16.sp,
                    )
                }
                Button(
                    onClick = { },
                    modifier = Modifier.padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSurface,
                        contentColor = Color(0xFF1D1B20)  )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.print),
                        contentDescription = "",
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = "Перепечатать",
                        fontSize = 16.sp,
                    )
                }
                Button(
                    onClick = { },
                    modifier = Modifier.padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSurface,
                        contentColor = Color(0xFF1D1B20)  )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.delete_white),
                        contentDescription = "",
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = "Списать",
                        fontSize = 16.sp,
                    )
                }
                Button(
                    onClick = { },
                    modifier = Modifier.padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSurface,
                        contentColor = Color(0xFF1D1B20)  )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = "",
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = "Редактировать",
                        fontSize = 16.sp,
                    )
                }
            }
        }
    }
}

