package com.ruparts.app.core.barcode

import javax.inject.Inject

class BarcodeTypeDetector @Inject constructor() {
    private val productCodeRegex = Regex("^TE.{16}$")
    private val locationCodeRegex = Regex("^#L(.{9}|.{11})$")

    fun detectCodeType(code: String): BarcodeType {
        return when {
            code.matches(productCodeRegex) -> BarcodeType.PRODUCT
            code.matches(locationCodeRegex) -> BarcodeType.LOCATION
            else -> BarcodeType.UNKNOWN
        }
    }
}