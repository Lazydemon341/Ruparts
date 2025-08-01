package com.ruparts.app.core.barcode.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.ui.unit.IntSize
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.io.ByteArrayOutputStream

class BarcodeImageAnalyzer(
    private val onBarcodesScanned: (List<String>) -> Unit
) : ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            //Barcode.FORMAT_CODE_128,
            //Barcode.FORMAT_DATA_MATRIX,
        )
        .build()
    private val scanner = BarcodeScanning.getClient(options)

    private var previewSize: IntSize = IntSize.Companion.Zero

    fun setPreviewSize(size: IntSize) {
        previewSize = size
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val bitmap = getBitmap(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val image = InputImage.fromBitmap(bitmap, 0)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        Log.d(TAG, "Barcodes detected: $barcodes")
                        onBarcodesScanned(barcodes.mapNotNull { it.rawValue })
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, "Barcode scanning failed", it)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun getBitmap(image: Image, rotationDegrees: Int): Bitmap {
        val bitmap = nv21ToBitmap(image)
        val correctedBitmap = matchPreviewImage(bitmap, rotationDegrees)
        return cropBitmap(correctedBitmap)
    }

    /**
     * https://stackoverflow.com/a/79464017
     */
    private fun nv21ToBitmap(image: Image): Bitmap {
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        // Copy Y, U, and V buffers into one array
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        // Convert NV21 format to Bitmap
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 100, out)

        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    /**
     * https://stackoverflow.com/a/60228420
     */
    private fun matchPreviewImage(original: Bitmap, rotationDegrees: Int): Bitmap {
        val rotationMatrix = Matrix().apply {
            postRotate(rotationDegrees.toFloat())
        }
        val rotatedBitmap = Bitmap.createBitmap(original, 0, 0, original.width, original.height, rotationMatrix, true)

        val cropHeight = if (rotatedBitmap.width < previewSize.width) {
            // if preview area larger than analysing image
            val koeff = rotatedBitmap.width.toFloat() / previewSize.width.toFloat()
            previewSize.height.toFloat() * koeff
        } else {
            // if preview area smaller than analysing image
            val prc = 100 - (previewSize.width.toFloat() / (rotatedBitmap.width.toFloat() / 100f))
            previewSize.height + ((previewSize.height.toFloat() / 100f) * prc)
        }

        val cropTop = (rotatedBitmap.height / 2f) - (cropHeight / 2f)
        return Bitmap.createBitmap(rotatedBitmap, 0, cropTop.toInt(), rotatedBitmap.width, cropHeight.toInt())
    }

    /**
     * https://stackoverflow.com/a/79464017
     */
    private fun cropBitmap(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val cropStart = (width * 0.25).toInt()  // 25% from the left
        val cropWidth = (width * 0.5).toInt()  // 50% width

        val cropTop = (height * 0.25).toInt()   // 25% from the top
        val cropHeight = (height * 0.5).toInt() // 50% height

        return Bitmap.createBitmap(bitmap, cropStart, cropTop, cropWidth, cropHeight)
    }

    companion object {
        private const val TAG = "QrCodeImageAnalyzer"

        fun getImageAnalysis(): ImageAnalysis {
            return ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_NV21)
                .build()
        }
    }
}