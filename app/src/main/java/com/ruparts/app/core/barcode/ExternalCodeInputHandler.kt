package com.ruparts.app.core.barcode

import android.util.Log
import android.view.KeyEvent

private const val QR_PREFIX = "&s"
private const val QR_SUFFIX = '&'

private const val LOG_TAG = "ExternalCodeInputHandler"

class ExternalCodeInputHandler(
    private val onCodeReceived: (String, BarcodeType) -> Unit,
) {

    private val barcodeTypeDetector = BarcodeTypeDetector()

    private var codeInput = ""
    private var inputInProgress: Boolean = false
    private var lastInputTime: Long? = null

    fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode != KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {

            Log.d(LOG_TAG, "keyEvent received: $event")
            val char = event.unicodeChar.toChar()
            handleInput(char)

            return true
        }
        return false
    }

    private fun handleInput(char: Char): Boolean {
        checkLastInputTime()

        return if (codeInput.isEmpty() && char == QR_PREFIX[0]) {

            codeInput = char.toString()
            true

        } else if (codeInput.length == 1 && char == QR_PREFIX[1]) {

            codeInput = ""
            inputInProgress = true
            true

        } else if (inputInProgress && char.isAllowedSymbol()) {

            codeInput += char
            true

        } else if (inputInProgress && char == QR_SUFFIX) {

            endInput()
            true

        } else {
            false
        }
    }

    private fun endInput() {
        Log.d(LOG_TAG, "code input finished: $codeInput")

        val codeType = barcodeTypeDetector.detectCodeType(codeInput)
        onCodeReceived(codeInput, codeType)

        inputInProgress = false
        codeInput = ""
    }

    private fun checkLastInputTime() {
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

    private fun Char.isAllowedSymbol(): Boolean {
        return this == '-' || this == '#' || this in 'A'..'Z' || this in '0'..'9'
    }
}