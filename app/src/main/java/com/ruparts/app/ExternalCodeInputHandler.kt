package com.ruparts.app

private const val QR_STARTING_CHARS = "&s"

class ExternalCodeInputHandler(
    private val onCodeReceived: (String) -> Unit,
) {

    private var codeInput = ""
    private var inputInProgress: Boolean = false
    private var lastInputTime: Long? = null

    fun handleInput(char: Char): Boolean {
        val currentTimeMillis = System.currentTimeMillis()
        val lastInputTimeMillis = lastInputTime ?: currentTimeMillis
        if (currentTimeMillis - lastInputTimeMillis >= 300L) {
            inputInProgress = false
            codeInput = ""
        }

        if (inputInProgress && char.isCapitalizedLetterOrNumber()) {
            codeInput += char

            // product code is 18 characters long
            if (codeInput.first() == 'T' && codeInput.length == 18) {

                onCodeReceived(codeInput)

                inputInProgress = false
                codeInput = ""
            }

            return true
        } else if (codeInput.isEmpty()
            && char == QR_STARTING_CHARS[0]
        ) {
            codeInput = char.toString()

            return true
        } else if (codeInput.length == 1
            && codeInput[0] == QR_STARTING_CHARS[0]
            && char == QR_STARTING_CHARS[1]
        ) {
            codeInput = ""
            inputInProgress = true

            return true
        }

        return false
    }

    private fun Char.isCapitalizedLetterOrNumber(): Boolean {
        return this in 'A'..'Z' || this in '0'..'9'
    }
}