package com.ruparts.app.core.barcode

import android.os.SystemClock
import android.util.Log
import javax.inject.Inject

private const val BARCODE_REQUIRED_FOCUS_DURATION = 1000L

class TrackBarcodeFocusUseCase @Inject constructor() {

    private var currentTrackedBarcode: String? = null
    private var barcodeFirstDetectedAt: Long = 0

    fun trackBarcode(
        barcode: String,
    ): Boolean {
        val currentTime = SystemClock.uptimeMillis()

        if (barcode == currentTrackedBarcode) {
            if (currentTime - barcodeFirstDetectedAt >= BARCODE_REQUIRED_FOCUS_DURATION) {
                // Barcode has been focused long enough, process it
                Log.d("TrackBarcodeFocusUseCase", "Barcode $barcode focused for ${BARCODE_REQUIRED_FOCUS_DURATION}ms, processing")
                currentTrackedBarcode = null
                return true
            }
        } else {
            // New barcode detected, start tracking it
            Log.d("TrackBarcodeFocusUseCase", "New barcode detected: $barcode, starting focus timer")
            currentTrackedBarcode = barcode
            barcodeFirstDetectedAt = currentTime
        }

        return false
    }
}