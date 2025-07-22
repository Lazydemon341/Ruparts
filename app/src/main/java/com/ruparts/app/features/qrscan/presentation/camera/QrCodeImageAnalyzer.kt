package com.ruparts.app.features.qrscan.presentation.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.io.ByteArrayOutputStream


internal class QrCodeImageAnalyzer(
    private val onBarcodesScanned: (List<Barcode>) -> Unit
) : ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            //Barcode.FORMAT_CODE_128,
            //Barcode.FORMAT_DATA_MATRIX,
        )
        .build()
    private val scanner = BarcodeScanning.getClient(options)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val croppedBitmap = cropImage(mediaImage)
            val image = InputImage.fromBitmap(
                croppedBitmap,
                imageProxy.imageInfo.rotationDegrees
            )

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        Log.d(TAG, "Barcodes detected: $barcodes")
                        onBarcodesScanned(barcodes)
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

    private fun cropImage(image: Image): Bitmap {
        val bitmap = imageToBitmap(image)
        return cropBitmap(bitmap)
    }

    /**
     * https://stackoverflow.com/a/79464017
     */
    private fun imageToBitmap(image: Image): Bitmap {
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
    }
}