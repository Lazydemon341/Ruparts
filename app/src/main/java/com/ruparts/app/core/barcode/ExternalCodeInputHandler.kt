package com.ruparts.app.core.barcode

import android.util.Log

private const val QR_PREFIX = "&s"
private const val QR_SUFFIX = '\n'

class ExternalCodeInputHandler(
    private val onCodeReceived: (String, BarcodeType) -> Unit,
) {

    private val barcodeTypeDetector = BarcodeTypeDetector()

    private var codeInput = ""
    private var inputInProgress: Boolean = false
    private var lastInputTime: Long? = null

    fun handleInput(char: Char): Boolean {

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

        } else if (inputInProgress && char == QR_SUFFIX) {

            handleInput()
            return true
        }

        return false
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

    private fun handleInput() {
        Log.d("ExternalCodeInputHandler", "code input finished: $codeInput")

        val codeType = barcodeTypeDetector.detectCodeType(codeInput)
        onCodeReceived(codeInput, codeType)

        inputInProgress = false
        codeInput = ""
    }

    private fun Char.isAllowedSymbol(): Boolean {
        return this == '-' || this == '#' || this in 'A'..'Z' || this in '0'..'9'
    }
}