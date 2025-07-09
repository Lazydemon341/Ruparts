package com.ruparts.app.features.cart.presentation

import android.content.Context
import android.util.AttributeSet
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.unit.dp
import com.ruparts.app.core.ui.theme.RupartsTheme

class CartItemCancelButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AbstractComposeView(context, attrs, defStyleAttr) {

    private val backgroundAnimatable = Animatable(0f)
    val animationState = mutableStateOf<Boolean>(false)

    private var onClickListener: (() -> Unit)? = null

    fun setOnClickListener(listener: () -> Unit) {
        onClickListener = listener
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        animationState.value = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animationState.value = false
    }

    @Composable
    override fun Content() {
        RupartsTheme {
            LaunchedEffect(animationState.value) {
                if (animationState.value == true) {
                    backgroundAnimatable.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(
                            durationMillis = 3000,
                            easing = LinearEasing,
                        ),
                    )
                } else {
                    backgroundAnimatable.snapTo(0f)
                }
            }
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
                contentPadding = PaddingValues(),
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    val backgroundColor = MaterialTheme.colorScheme.secondaryContainer
                    Canvas(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        drawRect(
                            color = backgroundColor,
                            size = Size(
                                width = size.width * backgroundAnimatable.value,
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