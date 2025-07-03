package com.ruparts.app.features.qrscan.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview as CameraPreview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ruparts.app.R
import com.ruparts.app.features.qrscan.model.ScannedItem
import com.ruparts.app.features.qrscan.presentation.camera.QrCodeImageAnalyzer
import java.util.concurrent.Executors

@Composable
fun QrScanScreen(scannedItems: List<ScannedItem>) {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf<Boolean>(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionGranted = isGranted
    }

    LaunchedEffect(Unit) {
        val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            permissionGranted = true
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    var surfaceRequestState = remember { mutableStateOf<SurfaceRequest?>(null) }

    LaunchedEffect(permissionGranted, context, lifecycleOwner) {
        if (permissionGranted) {
            startCamera(
                context = context,
                lifecycleOwner = lifecycleOwner,
                onSurfaceRequest = { surfaceRequestState.value = it },
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val surfaceRequest = surfaceRequestState.value
        if (surfaceRequest != null) {
            CameraXViewfinder(
                surfaceRequest = surfaceRequest,
                modifier = Modifier,
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            )
        }
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.5f),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            shape = MaterialTheme.shapes.extraLarge
        ) {
            if (scannedItems.isEmpty()) {
                QrScanEmptyContent()
            } else {
                QrScanItemsContent(scannedItems)
            }
        }
    }
}

@Composable
private fun QrScanItemsContent(scannedItems: List<ScannedItem>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxSize()
        ) {

        items(scannedItems) { item ->
            QrScanListItem(item)
            Spacer(modifier = Modifier.height(8.dp))
        }

    }
    Button(
        onClick = {},
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = "Закончить",
            fontSize = 16.sp,
        )
    }
}
}

@Composable
private fun QrScanListItem(item: ScannedItem) {

    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.StartToEnd)
            else if (it == SwipeToDismissBoxValue.EndToStart) onRemove(item)
            it != SwipeToDismissBoxValue.StartToEnd
        }
    )

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        modifier = Modifier.fillMaxSize(),
        backgroundContent = {
            when (swipeToDismissBoxState.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd -> {
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    Icon(
                        painter = painterResource(id = R.drawable.delete_white),
                        contentDescription = "Remove item",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFB3261E))
                            .wrapContentSize(Alignment.CenterEnd)
                            .padding(12.dp),
                        tint = Color.White
                    )
                }
                SwipeToDismissBoxValue.Settled -> {}
            }
        }
    ) {

        Column(
            modifier = Modifier.background(Color(0xFFFEF7FF))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.article,
                    color = Color(0xFF1D1B20),
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    fontSize = 22.sp
                )
                Text(
                    text = item.quantity.toString(),
                    color = Color(0xFF1D1B20),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .border(1.dp, SolidColor(Color.Black), RoundedCornerShape(percent = 20))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }

            Text(
                text = item.brand,
                color = Color(0xFF1D1B20),
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = item.description,
                color = Color(0xFF1D1B20),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun onRemove(item: ScannedItem) {
    mockScannedItems -= item
}


@Composable
private fun QrScanEmptyContent() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.qrscan),
                contentDescription = "Картинка",
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Отсканируйте товары и они появятся в списке",
                color = colorResource(id = R.color.secondary60),
                fontSize = 14.sp
            )
        }
    }
}

private suspend fun startCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    onSurfaceRequest: (SurfaceRequest) -> Unit,
) {
    val processCameraProvider = ProcessCameraProvider.awaitInstance(context.applicationContext)
    processCameraProvider.unbindAll()

    val cameraPreviewUseCase = CameraPreview.Builder().build().apply {
        setSurfaceProvider(onSurfaceRequest)
    }
    val imageAnalysisUseCase = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .apply {
            setAnalyzer(
                Executors.newSingleThreadExecutor(),
                QrCodeImageAnalyzer(
                    onBarcodeDetected = { barcode ->
                        barcode.rawValue?.let { qrContent ->
                            // TODO: onQrCodeDetected(qrContent)
                        }
                    }
                )
            )
        }
    processCameraProvider.bindToLifecycle(
        lifecycleOwner,
        DEFAULT_BACK_CAMERA,
        cameraPreviewUseCase,
        imageAnalysisUseCase,
    )
}

@Preview
@Composable
fun QrScanScreenPreview() {
    QrScanScreen(scannedItems = mockScannedItems)
}

@Composable
fun TodoListItem(
    item: ScannedItem,
    onToggleDone: (ScannedItem) -> Unit,
    onRemove: (ScannedItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.StartToEnd) onToggleDone(item)
            else
                if (it == SwipeToDismissBoxValue.EndToStart) onRemove(item)
            it != SwipeToDismissBoxValue.StartToEnd
        }
    )

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        modifier = modifier.fillMaxSize(),
        backgroundContent = {
            when (swipeToDismissBoxState.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd -> {
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove item",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Red)
                            .wrapContentSize(Alignment.CenterEnd)
                            .padding(12.dp),
                        tint = Color.White
                    )
                }
                SwipeToDismissBoxValue.Settled -> {}
            }
        }
    ) {
        QrScanListItem(item)
    }
}


