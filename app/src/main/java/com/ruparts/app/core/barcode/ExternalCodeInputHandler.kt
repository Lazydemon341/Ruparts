package com.ruparts.app.core.barcode

import android.util.Log
import android.view.KeyEvent

private const val QR_PREFIX = "&s"
private const val QR_SUFFIX = '\n'

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

            if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
                endInput()
            } else {
                val char = event.unicodeChar.toChar()
                handleInput(char)
            }

            return true
        }
        return false
    }

    private fun handleInput(char: Char): Boolean {
        checkLastInputTime()

        if (codeInput.isEmpty() && char == QR_PREFIX[0]) {

            codeInput = char.toString()
            return true

        } else if (codeInput.length == 1 && char == QR_PREFIX[1]) {

            codeInput = ""
            inputInProgress = true
            return true

        } else if (inputInProgress && char.isAllowedSymbol()) {

            codeInput += char
            return true
        }

        return false
    }

    private fun endInput() {
        checkLastInputTime()

        if (inputInProgress) {
            Log.d(LOG_TAG, "code input finished: $codeInput")

            val codeType = barcodeTypeDetector.detectCodeType(codeInput)
            onCodeReceived(codeInput, codeType)

            inputInProgress = false
            codeInput = ""
        }
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