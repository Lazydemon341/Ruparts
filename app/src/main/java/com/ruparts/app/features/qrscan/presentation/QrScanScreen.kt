package com.ruparts.app.features.qrscan.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
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
import com.ruparts.app.core.ui.components.RupartsCartItem
import com.ruparts.app.features.cart.model.CartListItem
import com.ruparts.app.features.cart.model.CartScanPurpose
import com.ruparts.app.features.qrscan.presentation.camera.QrCodeImageAnalyzer
import com.ruparts.app.features.qrscan.presentation.model.QrScanScreenAction
import com.ruparts.app.features.qrscan.presentation.model.QrScanScreenState
import java.util.concurrent.Executors
import androidx.camera.core.Preview as CameraPreview

@Composable
fun QrScanScreen(
    state: QrScanScreenState,
    onAction: (QrScanScreenAction) -> Unit,
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
        QrCodeImageAnalyzer(
            onBarcodesScanned = { onAction(QrScanScreenAction.BarcodesScanned(it)) },
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
        topBar = {
            QrScanScreenTopBar(
                camera = cameraState.value,
                scannedItemsCount = state.scannedItems.size,
                onAction = onAction,
                onShowInputDialog = {
                    showInputDialog = true
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        containerColor = Color.Black,
        contentWindowInsets = WindowInsets.systemBars.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
        )
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f),
            ) {
                val surfaceRequest = surfaceRequestState.value
                if (surfaceRequest != null) {
                    CameraPreview(
                        surfaceRequest = surfaceRequest,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .onGloballyPositioned { coordinates ->
                                imageAnalyzer.setPreviewSize(coordinates.size)
                            },
                    )
                }

                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.Black.copy(alpha = 0.5f))
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.55f),
                color = MaterialTheme.colorScheme.surfaceContainer,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            ) {
                if (state.scannedItems.isEmpty()) {
                    QrScanEmptyContent()
                } else {
                    QrScanItemsContent(
                        scannedItems = state.scannedItems,
                        onRemove = { onAction(QrScanScreenAction.RemoveItem(it)) },
                        onTransferToCart = { onAction(QrScanScreenAction.OnTransferToCart) },
                        showTransferToBasketButton = state.purpose == CartScanPurpose.TRANSFER_TO_CART,
                        headerText = when (state.purpose) {
                            CartScanPurpose.TRANSFER_TO_CART -> "Отсканируйте товары в ячейке, они попадут в корзину"
                            CartScanPurpose.TRANSFER_TO_LOCATION -> "Отсканируйте один или несколько товаров,\n" +
                                    "а затем ячейку или отгрузочное место"
                        },
                    )
                }
            }

            if (showInputDialog) {
                ManualInputDialog(
                    onDismiss = {
                        showInputDialog = false
                    },
                    onConfirmInput = { input ->
                        onAction(QrScanScreenAction.ManualInput(input))
                    },
                )
            }
        }
    }
}

@Composable
private fun CameraPreview(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QrScanScreenTopBar(
    camera: Camera?,
    scannedItemsCount: Int,
    onAction: (QrScanScreenAction) -> Unit,
    onShowInputDialog: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            QrScanScreenTitle(scannedItemsCount)
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
                    onShowInputDialog()
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Keyboard,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            FlashButton(camera = camera)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White,
            navigationIconContentColor = Color.White
        ),
    )
}

@Composable
private fun QrScanScreenTitle(scannedItemsCount: Int) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = if (scannedItemsCount == 0) "Скан" else "Товары",
            color = Color.White,
            fontSize = 22.sp,
        )
        if (scannedItemsCount > 0) {
            Text(
                text = "Отсканировано: ${scannedItemsCount}",
                color = Color.White,
                fontSize = 12.sp,
            )
        }
    }
}

private const val listItemContentType = "listItem"
private const val listHeaderContentType = "listHeader"

@Composable
private fun QrScanItemsContent(
    scannedItems: List<CartListItem>,
    showTransferToBasketButton: Boolean,
    headerText: String,
    onRemove: (CartListItem) -> Unit,
    onTransferToCart: () -> Unit,
) {
    val lazyListState = rememberLazyListState()

    LaunchedEffect(scannedItems) {
        lazyListState.scrollToItem(scannedItems.lastIndex)
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        LazyColumn(
            modifier = Modifier.align(Alignment.TopCenter),
            state = lazyListState,
            reverseLayout = true
        ) {
            itemsIndexed(
                items = scannedItems,
                key = { _, item -> item.id },
                contentType = { _, _ -> listItemContentType },
            ) { index, item ->
                if (index == 0 && showTransferToBasketButton) {
                    Spacer(modifier = Modifier.height(88.dp))
                }
                RupartsCartItem(
                    item = item,
                    onRemove = onRemove,
                    enableSwipeToDismiss = index == scannedItems.lastIndex,
                    isRowVisible = index == scannedItems.lastIndex,
                    modifier = Modifier.animateItem()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            stickyHeader(
                contentType = listHeaderContentType,
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 4.dp)
                        .animateItem(),
                    color = colorResource(R.color.neutral60),
                    text = headerText,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }
        }
        if (showTransferToBasketButton) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                shape = RectangleShape,
            ) {
                Button(
                    onClick = onTransferToCart,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 8.dp, bottom = 24.dp)
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text(
                        text = "Переместить в корзину",
                        fontSize = 16.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun QrScanEmptyContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = Color.Gray
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .size(20.dp),
                    tint = Color.Gray
                )
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = Color.Gray
                )
            }
            Text(
                text = "Из ячейки в корзину",
                fontSize = 16.sp,
                color = colorResource(id = R.color.secondary60),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W500,
            )
            Text(
                text = "Отсканируйте товары в ячейке, они попадут в корзину",
                color = colorResource(id = R.color.secondary60),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
        Text(
            text = "или",
            color = colorResource(id = R.color.secondary60),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = Color.Gray
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .size(20.dp),
                    tint = Color.Gray
                )
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = Color.Gray
                )
            }
            Text(
                text = "Из корзины в ячейку",
                fontSize = 16.sp,
                color = colorResource(id = R.color.secondary60),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W500,
            )
            Text(
                text = "Отсканируйте товары в корзине,\n" +
                        "а затем ячейку",
                color = colorResource(id = R.color.secondary60),
                style = MaterialTheme.typography.bodyMedium,
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

private suspend fun startCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    imageAnalyzer: ImageAnalysis.Analyzer,
    onSurfaceRequest: (SurfaceRequest) -> Unit,
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
        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_NV21)
        .build()
        .apply {
            setAnalyzer(
                Executors.newSingleThreadExecutor(),
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

@Preview
@Composable
private fun QrScanScreenPreview() {
    QrScanScreen(
        state = QrScanScreenState(
            scannedItems = listOf(
                CartListItem(
                    id = 0,
                    article = "11115555669987452131",
                    brand = "Toyota",
                    quantity = 13481,
                    description = "Описание",
                    barcode = "",
                    cartOwner = "",
                    info = "",
                ),
                CartListItem(
                    id = 1,
                    article = "548870578",
                    brand = "Mazda",
                    quantity = 10,
                    description = "Длинное описание, которое не влезает в одну строчку",
                    barcode = "",
                    cartOwner = "",
                    info = "",
                ),
                CartListItem(
                    id = 2,
                    article = "36575",
                    brand = "Porsche",
                    quantity = 5843,
                    description = "Очень длинное описание, которое не влезает в одну строчку, " +
                            "которое не влезает в одну строчку, которое не влезает в одну строчку, " +
                            "которое не влезает в одну строчку,",
                    barcode = "",
                    cartOwner = "",
                    info = "",
                )
            ),
            isLoading = false,
            purpose = CartScanPurpose.TRANSFER_TO_CART
        ),
        onAction = {},
        snackbarHostState = remember { SnackbarHostState() }
    )
}

@Preview
@Composable
private fun QrScanScreenEmptyPreview() {
    QrScanScreen(
        state = QrScanScreenState(
            scannedItems = emptyList(),
            isLoading = false,
            purpose = CartScanPurpose.TRANSFER_TO_CART,
        ),
        onAction = {},
        snackbarHostState = remember { SnackbarHostState() }
    )
}

@Preview
@Composable
private fun QrScanScreenLoadingPreview() {
    QrScanScreen(
        state = QrScanScreenState(
            scannedItems = emptyList(),
            isLoading = true,
            purpose = CartScanPurpose.TRANSFER_TO_CART,
        ),
        onAction = {},
        snackbarHostState = remember { SnackbarHostState() }
    )
}