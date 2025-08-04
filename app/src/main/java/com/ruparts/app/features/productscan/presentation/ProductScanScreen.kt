package com.ruparts.app.features.productscan.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.ruparts.app.R
import com.ruparts.app.core.barcode.camera.BarcodeImageAnalyzer
import com.ruparts.app.core.ui.components.CameraPreview
import com.ruparts.app.core.ui.theme.RupartsTheme
import com.ruparts.app.features.productscan.presentation.model.ProductScanScreenAction
import com.ruparts.app.features.productscan.presentation.model.ProductScanScreenState
import androidx.camera.core.Preview as CameraPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScanScreen(
    state: ProductScanScreenState,
    onAction: (ProductScanScreenAction) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    var permissionGranted by remember { mutableStateOf<Boolean>(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionGranted = isGranted
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            permissionGranted = true
        }
    }

    var surfaceRequestState = remember { mutableStateOf<SurfaceRequest?>(null) }
    var cameraState = remember { mutableStateOf<Camera?>(null) }
    val imageAnalyzer = remember {
        BarcodeImageAnalyzer(
            onBarcodesScanned = {
                onAction(ProductScanScreenAction.BarcodesScanned(it))
            }
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(permissionGranted, context, lifecycleOwner) {
        if (permissionGranted) {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                cameraState.value = startCamera(
                    context = context,
                    lifecycleOwner = lifecycleOwner,
                    imageAnalyzer = imageAnalyzer,
                    onSurfaceRequest = { surfaceRequestState.value = it },
                )
            }
        }
    }

    var showInputDialog by remember { mutableStateOf(false) }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopBar(
                onAction = onAction,
                onKeyboardClick = {
                    showInputDialog = true
                },
                camera = cameraState.value,
            )
        },
        contentColor = Color.Black,
        containerColor = Color.Black,
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Box(modifier = Modifier.weight(1f)) {
                val surfaceRequest = surfaceRequestState.value
                if (surfaceRequest != null) {
                    CameraPreview(
                        surfaceRequest = surfaceRequest,
                        modifier = Modifier
                            .fillMaxSize()
                            .onGloballyPositioned { coordinates ->
                                imageAnalyzer.setPreviewSize(coordinates.size)
                            }
                    )
                }

                if (state.isScanning) {
                    LoadingOverlay()
                }
            }
            BottomTexts()
        }

        if (showInputDialog) {
            ManualInputDialog(
                onDismiss = {
                    showInputDialog = false
                },
                onConfirmInput = { input ->
                    onAction(ProductScanScreenAction.ManualInput(input))
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    onAction: (ProductScanScreenAction) -> Unit,
    onKeyboardClick: () -> Unit,
    camera: Camera?,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Товар",
                color = Color.White,
                fontWeight = FontWeight.W400
            )
        },
        navigationIcon = {
            IconButton(
                onClick = { onAction(ProductScanScreenAction.OnBackPressed) }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(
                onClick = onKeyboardClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Keyboard,
                    contentDescription = "Manual input",
                    tint = Color.White
                )
            }
            FlashButton(camera = camera)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black,
        ),
    )
}

@Composable
private fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            trackColor = MaterialTheme.colorScheme.secondaryContainer,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun BottomTexts() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(horizontal = 16.dp)
            .padding(top = 40.dp, bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.QrCodeScanner,
            contentDescription = "Barcode scanner",
            tint = colorResource(R.color.neutral60),
            modifier = Modifier.size(44.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Отсканируйте товар,\nчтобы найти его в базе данных",
            color = colorResource(R.color.neutral60),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun FlashButton(camera: Camera?) {
    if (camera != null && camera.cameraInfo.hasFlashUnit()) {
        val torchStateLiveData = remember(camera) {
            camera.cameraInfo.torchState
        }
        val torchState by torchStateLiveData.observeAsState()
        IconButton(
            onClick = {
                val enabled = torchState == TorchState.ON
                camera.cameraControl.enableTorch(!enabled)
            },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = when (torchState) {
                    TorchState.ON -> Icons.Filled.Bolt
                    else -> Icons.Outlined.Bolt
                },
                contentDescription = "Flash",
                tint = Color.White
            )
        }
    }
}

private suspend fun startCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    imageAnalyzer: androidx.camera.core.ImageAnalysis.Analyzer,
    onSurfaceRequest: (SurfaceRequest) -> Unit,
): Camera {
    val processCameraProvider = ProcessCameraProvider.awaitInstance(context.applicationContext)

    processCameraProvider.unbindAll()

    val cameraPreviewUseCase = CameraPreview.Builder()
        .build()
        .apply {
            setSurfaceProvider(onSurfaceRequest)
        }
    val imageAnalysisUseCase = BarcodeImageAnalyzer.getImageAnalysis()
        .apply {
            setAnalyzer(
                java.util.concurrent.Executors.newSingleThreadExecutor(),
                imageAnalyzer
            )
        }

    return processCameraProvider.bindToLifecycle(
        lifecycleOwner,
        DEFAULT_BACK_CAMERA,
        cameraPreviewUseCase,
        imageAnalysisUseCase,
    )
}

@Composable
private fun ManualInputDialog(
    onDismiss: () -> Unit,
    onConfirmInput: (text: String) -> Unit,
) {
    var inputText by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = {
            inputText = ""
            onDismiss()
        },
        title = {
            Text(
                text = "Ручной ввод кода",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            val focusRequester = remember { FocusRequester() }

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it.uppercase() },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                label = { Text("Штрихкод") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = false
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onConfirmInput(inputText)
                        inputText = ""
                    }
                    onDismiss()
                }
            ) {
                Text("Применить")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    inputText = ""
                    onDismiss()
                }
            ) {
                Text("Отмена")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ProductScanScreenPreview() {
    RupartsTheme {
        ProductScanScreen(
            state = ProductScanScreenState(),
            onAction = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}
