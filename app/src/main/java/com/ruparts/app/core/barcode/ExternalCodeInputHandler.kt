package com.ruparts.app.core.barcode

import android.os.Handler
import android.os.Looper
import android.util.Log

private const val QR_STARTING_CHARS = "&s"
private const val PRODUCT_FIRST_SYMBOL = 'T'
private const val LOCATION_FIRST_SYMBOL = '#'

class ExternalCodeInputHandler(
    private val onCodeReceived: (String, BarcodeType) -> Unit,
) {

    private var codeInput = ""
    private var inputInProgress: Boolean = false
    private var lastInputTime: Long? = null

    private val handler = Handler(Looper.getMainLooper())

    fun handleInput(char: Char): Boolean {

        checkTime()

        if (codeInput.isEmpty()
            && char == QR_STARTING_CHARS[0]
        ) {
            codeInput = char.toString()

            return true
        } else if (codeInput.length == 1
            && char == QR_STARTING_CHARS[1]
        ) {
            codeInput = ""
            inputInProgress = true

            return true
        } else if (inputInProgress && char.isAllowedSymbol()) {
            handler.removeCallbacksAndMessages(null)

            codeInput += char

            Log.d("ExternalCodeInputHandler", "codeInput: $codeInput")

            if (!checkProductCode()) {
                postCheckLocationCode(codeInput)
            }

            return true
        }

        return false
    }

    private fun checkTime() {
        val currentTimeMillis = System.currentTimeMillis()
        val lastInputTimeMillis = lastInputTime ?: currentTimeMillis
        // check if last input was more than 300ms ago.
        // If so, drop last input values.
        if (currentTimeMillis - lastInputTimeMillis >= 300L) {
            inputInProgress = false
            codeInput = ""
        }
        lastInputTime = currentTimeMillis
    }

    private fun postCheckLocationCode(code: String) {
        if (code.length in (3..16)) {
            handler.postDelayed(
                { checkLocationCode(code) },
                300L
            )
        }
    }

    private fun checkLocationCode(code: String) {
        // location code is from 3 to 16 characters long
        if (code.first() == LOCATION_FIRST_SYMBOL) {

            onCodeReceived(codeInput, BarcodeType.LOCATION)

            inputInProgress = false
            codeInput = ""
        }
    }

    private fun checkProductCode(): Boolean {
        // product code is 18 characters long
        if (codeInput.first() == PRODUCT_FIRST_SYMBOL && codeInput.length == 18) {

            onCodeReceived(codeInput, BarcodeType.PRODUCT)

            inputInProgress = false
            codeInput = ""

            return true
        }
        return false
    }

    private fun Char.isAllowedSymbol(): Boolean {
        return this == '-' || this == '#' || this in 'A'..'Z' || this in '0'..'9'
    }
}