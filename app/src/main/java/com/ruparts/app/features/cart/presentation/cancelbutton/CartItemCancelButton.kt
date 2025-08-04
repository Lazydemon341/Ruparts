package com.ruparts.app.features.cart.presentation.cancelbutton

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.unit.dp
import com.ruparts.app.core.ui.theme.RupartsTheme
import kotlinx.coroutines.flow.StateFlow

class CartItemCancelButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AbstractComposeView(context, attrs, defStyleAttr) {

    private var loaderState: StateFlow<Float>? = null

    private var onClickListener: (() -> Unit)? = null

    fun setOnClickListener(listener: () -> Unit) {
        onClickListener = listener
    }

    fun setLoaderState(loaderState: StateFlow<Float>?) {
        this.loaderState = loaderState
    }

    @Composable
    override fun Content() {
        RupartsTheme {
            val loaderState = loaderState?.collectAsState()

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(16.dp),
                onClick = { onClickListener?.invoke() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    val backgroundColor = MaterialTheme.colorScheme.secondaryContainer
                    Canvas(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        drawRect(
                            color = backgroundColor,
                            size = Size(
                                width = size.width * (loaderState?.value ?: 0f),
                                height = size.height,
                            )
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
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Отменить добавление")
                    }
                }
            }
        }
    }
}