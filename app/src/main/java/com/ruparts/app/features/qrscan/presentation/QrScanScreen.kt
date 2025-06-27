package com.ruparts.app.features.qrscan.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ruparts.app.features.qrscan.presentation.camera.QrCodeImageAnalyzer
import java.util.concurrent.Executors

@Composable
fun QrScanScreen() {
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
            // Empty bottom sheet content
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

    val cameraPreviewUseCase = Preview.Builder().build().apply {
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