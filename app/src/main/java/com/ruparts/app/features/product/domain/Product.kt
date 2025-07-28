package com.ruparts.app.features.product.domain

import com.ruparts.app.features.commonlibrary.ProductFlag
import java.time.LocalDate

data class Product(
    val unitId: Int,
    val vendorCode: String,
    val brand: String,
    val description: String?,
    val quantity: Int,
    val barcode: String,
    val location: String,
    val acceptedAt: LocalDate?,
    val unitComment: String?,
    val flags: List<ProductFlag>,
    val photos: List<ProductPhotoItem>,
    val card: ProductCard?,
    val defect: ProductDefect?,
    val actions: ProductActions?
)

data class ProductCard(
    val id: Int,
    val weight: Int?,
    val sizeHeight: Int?,
    val sizeWidth: Int?,
    val sizeLength: Int?,
    val comment: String?,
    val flags: List<ProductFlag>,
    val photos: List<ProductPhotoItem>,
)

data class ProductDefect(
    val comment: String?,
    val photos: List<ProductPhotoItem>,
)

data class ProductActions(
    val split: Boolean?,
    val notFound: Boolean?,
    val reprint: Boolean?,
    val defect: Boolean?,
    val suspectDefect: Boolean?,
    val writeOff: Boolean?
)

data class ProductPhotoItem(val id: String, val url: String)