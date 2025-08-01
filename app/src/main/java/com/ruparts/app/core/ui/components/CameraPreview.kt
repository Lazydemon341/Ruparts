package com.ruparts.app.core.ui.components

import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceRequest
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath

@Composable
fun CameraPreview(
    surfaceRequest: SurfaceRequest,
    modifier: Modifier = Modifier,
) {
    CameraXViewfinder(
        surfaceRequest = surfaceRequest,
        modifier = modifier
            .fillMaxSize()
            .drawWithCache {
                val rect = Rect(
                    left = size.width * 0.25f,
                    right = size.width * 0.75f,
                    top = size.height * 0.25f,
                    bottom = size.height * 0.75f,
                )
                val rectPath = Path().apply {
                    addRect(rect)
                }
                val rectColor = SolidColor(Color.Black.copy(alpha = 0.5f))
                val strokeStyle = Stroke(width = 4f)

                onDrawWithContent {
                    drawContent()
                    clipPath(rectPath, clipOp = ClipOp.Difference) {
                        drawRect(rectColor)
                        drawPath(rectPath, style = strokeStyle, color = Color.White)
                    }
                }
            },
    )
}