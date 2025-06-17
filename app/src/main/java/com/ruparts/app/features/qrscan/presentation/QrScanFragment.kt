package com.ruparts.app.features.qrscan.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ruparts.app.R
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class QrScanFragment : Fragment() {

    private val viewModel: QrScanViewModel by viewModels()

    private lateinit var closeButton: ImageButton
    private lateinit var previewView: PreviewView

    private var imageAnalysisExecutor: ExecutorService? = null
    private var processCameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(
                requireContext(),
                R.string.permission_denied,
                Toast.LENGTH_SHORT
            ).show()
            findNavController().popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_qr_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageAnalysisExecutor = Executors.newSingleThreadExecutor()

        closeButton = view.findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        previewView = view.findViewById(R.id.previewView)

        if (permissionsGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val listener = Runnable {
            processCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            imageAnalysis.setAnalyzer(
                requireNotNull(imageAnalysisExecutor),
                QrCodeImageAnalyzer(
                    onQrCodesDetected = { codes ->
                        codes.firstOrNull()?.let { barcode ->
                            barcode.rawValue?.let { qrContent ->
                                handleQrCodeResult(qrContent)
                            }
                        }
                    }
                )
            )

            processCameraProvider?.unbindAll()
            camera = processCameraProvider?.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalysis
            )
        }
        cameraProviderFuture.addListener(
            listener,
            ContextCompat.getMainExecutor(requireContext())
        )
    }

    private fun zoomControl() {
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val camera = requireNotNull(camera)
                val zoomState = requireNotNull(camera.cameraInfo.zoomState.value)
                val scale = zoomState.zoomRatio * detector.scaleFactor
                camera.cameraControl.setZoomRatio(scale)
//              TODO:
//                val maxZoom = requireNotNull(zoomState).maxZoomRatio.toDouble()
//                if (scale in 1.0..maxZoom) {
//                    zoomInfo.text = String.format("%.1f", scale) + "x"
//                }
                return true
            }
        }
        val scaleGestureDetector = ScaleGestureDetector(requireContext(), listener)
        previewView.setOnTouchListener { view, event ->
            scaleGestureDetector.onTouchEvent(event)
            view.performClick()
            return@setOnTouchListener true
        }
    }

    private fun handleQrCodeResult(qrContent: String) {
        // To avoid multiple scans of the same QR code
        processCameraProvider?.unbindAll()

        Log.d(TAG, "QR Code scanned: $qrContent")

        // TODO: For now, just navigate back to the tasks list
        // In a real implementation, you might want to process the QR code content
        // or pass it to another fragment
        findNavController().popBackStack()
    }

    private fun permissionsGranted(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        )
        return cameraPermission == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        processCameraProvider?.unbindAll()
        imageAnalysisExecutor?.shutdown()
    }

    companion object {
        private const val TAG = "QrScanFragment"
    }
}
