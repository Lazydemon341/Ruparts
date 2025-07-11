package com.ruparts.app.features.qrscan.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.common.Barcode
import com.ruparts.app.R
import com.ruparts.app.features.qrscan.model.ScannedItem
import com.ruparts.app.features.qrscan.presentation.camera.QrCodeImageAnalyzer
import java.util.concurrent.Executors
import androidx.camera.core.Preview as CameraPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScanScreen(
    scannedItems: List<ScannedItem>,
    onAction: (QrScanScreenAction) -> Unit,
) {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf<Boolean>(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionGranted = isGranted
    }

    val activity = LocalActivity.current
    val view = LocalView.current

    DisposableEffect(activity, view) {
        val window = requireNotNull(activity?.window)
        val windowInsetsController = WindowInsetsControllerCompat(window, view)
        windowInsetsController.apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }

        onDispose {
            windowInsetsController.apply {
                isAppearanceLightStatusBars = true
                isAppearanceLightNavigationBars = true
            }
        }
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
    var cameraState = remember { mutableStateOf<Camera?>(null) }

    LaunchedEffect(permissionGranted, context, lifecycleOwner) {
        if (permissionGranted) {
            cameraState.value = startCamera(
                context = context,
                lifecycleOwner = lifecycleOwner,
                onSurfaceRequest = { surfaceRequestState.value = it },
                onBarcodesScanned = { onAction(QrScanScreenAction.BarcodesScanned(it)) },
            )
        }
    }

    var showInputDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = if (scannedItems.isEmpty()) "Скан" else "Товары",
                            color = Color.White,
                            fontSize = 22.sp,
                        )
                        if (scannedItems.isNotEmpty()) {
                            Text(
                                text = "Отсканировано: ${scannedItems.size}",
                                color = Color.White,
                                fontSize = 12.sp,
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onAction(QrScanScreenAction.BackClick) },
                        modifier = Modifier.size(48.dp)
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
                        onClick = {
                            showInputDialog = true
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    FlashButton(camera = cameraState.value)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
            )
        },
        containerColor = Color.Black,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val surfaceRequest = surfaceRequestState.value
            if (surfaceRequest != null) {
                CameraXViewfinder(
                    surfaceRequest = surfaceRequest,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f),
                color = MaterialTheme.colorScheme.surfaceContainer,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            ) {
                if (scannedItems.isEmpty()) {
                    QrScanEmptyContent()
                } else {
                    QrScanItemsContent(
                        scannedItems = scannedItems,
                        onRemove = { onAction(QrScanScreenAction.RemoveItem(it)) },
                    )
                }
            }
        }

        if (showInputDialog) {
            ManualInputDialog(
                onDismiss = {
                    showInputDialog = false
                },
            )
        }
    }
}

@Composable
private fun QrScanItemsContent(scannedItems: List<ScannedItem>, onRemove: (ScannedItem) -> Unit) {
    val lazyListState = rememberLazyListState()

    LaunchedEffect(scannedItems) {
        lazyListState.scrollToItem(scannedItems.lastIndex)
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.align(Alignment.TopCenter),
            state = lazyListState,
            reverseLayout = true
        ) {
            itemsIndexed(
                items = scannedItems,
                key = { _, it -> it.article },
            ) { index, item ->
                QrScanListItem(
                    item = item,
                    onRemove = onRemove,
                    enableSwipeToDismiss = index == scannedItems.lastIndex,
                    modifier = Modifier.animateItem()
                )
                if (index == scannedItems.lastIndex) {
                    Spacer(modifier = Modifier.height(12.dp))
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

        }
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            shape = RectangleShape,
        ) {
            Button(
                onClick = {},
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 8.dp, bottom = 24.dp)
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
}

@Composable
private fun QrScanListItem(
    item: ScannedItem,
    onRemove: (ScannedItem) -> Unit,
    enableSwipeToDismiss: Boolean,
    modifier: Modifier = Modifier,
) {

    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onRemove(item)
            }
            value != SwipeToDismissBoxValue.StartToEnd
        },
        positionalThreshold = with(LocalDensity.current) { { 160.dp.toPx() } }
    )

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        modifier = modifier.fillMaxSize(),
        enableDismissFromStartToEnd = false,
        gesturesEnabled = enableSwipeToDismiss,
        backgroundContent = {
            if (swipeToDismissBoxState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
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
        }
    ) {
        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(vertical = 12.dp, horizontal = 16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.article,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    fontSize = 22.sp
                )
                Text(
                    text = item.quantity.toString(),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .border(1.dp, SolidColor(Color.Black), RoundedCornerShape(percent = 20))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }

            Text(
                text = item.brand,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = item.description,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun QrScanEmptyContent() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.qrscan),
                contentDescription = "Картинка",
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Отсканируйте товары в ячейке, чтобы переместить их в корзину " +
                        "или отсканируйте товары в корзине, чтобы переместить их в ячейку",
                color = colorResource(id = R.color.secondary60),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
        }
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
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
private fun ManualInputDialog(
    onDismiss: () -> Unit,
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
                onValueChange = { inputText = it },
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
                        // TODO: Handle input confirmation
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

private suspend fun startCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    onSurfaceRequest: (SurfaceRequest) -> Unit,
    onBarcodesScanned: (List<Barcode>) -> Unit,
): Camera {
    val processCameraProvider = ProcessCameraProvider.awaitInstance(context.applicationContext)
    processCameraProvider.unbindAll()

    val cameraPreviewUseCase = CameraPreview.Builder()
        .build()
        .apply {
            setSurfaceProvider(onSurfaceRequest)
        }
    val imageAnalysisUseCase = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .apply {
            setAnalyzer(
                Executors.newSingleThreadExecutor(),
                QrCodeImageAnalyzer(
                    onBarcodesScanned = onBarcodesScanned,
                )
            )
        }
    return processCameraProvider.bindToLifecycle(
        lifecycleOwner,
        DEFAULT_BACK_CAMERA,
        cameraPreviewUseCase,
        imageAnalysisUseCase,
    )
}

@Preview
@Composable
fun QrScanScreenPreview() {
    QrScanScreen(
        scannedItems = mockScannedItems,
        onAction = {},
    )
}



