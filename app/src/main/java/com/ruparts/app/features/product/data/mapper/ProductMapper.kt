package com.ruparts.app.features.product.data.mapper

import com.ruparts.app.features.product.data.network.model.ProductActionsDto
import com.ruparts.app.features.product.data.network.model.ProductCardDto
import com.ruparts.app.features.product.data.network.model.ProductDefectDto
import com.ruparts.app.features.product.data.network.model.ProductDto
import com.ruparts.app.features.product.domain.Product
import com.ruparts.app.features.product.domain.ProductActions
import com.ruparts.app.features.product.domain.ProductCard
import com.ruparts.app.features.product.domain.ProductDefect
import com.ruparts.app.features.product.domain.ProductPhotoItem
import javax.inject.Inject

class ProductMapper @Inject constructor() {
    fun mapProduct(dto: ProductDto): Product = Product(
        unitId = dto.unitId,
        vendorCode = dto.vendorCode,
        brand = dto.brand,
        description = dto.description,
        quantity = dto.quantity,
        barcode = dto.barcode,
        location = dto.location,
        acceptedAt = dto.acceptedAt,
        unitComment = dto.unitComment,
        flags = dto.flags,
        photos = dto.photos?.map { ProductPhotoItem(it.key, it. value) }.orEmpty(),
        card = dto.card?.let { mapCard(it) },
        defect = dto.defect?.let { mapDefect(it) },
        actions = dto.actions?.let { mapActions(it) }
    )

    private fun mapCard(dto: ProductCardDto): ProductCard = ProductCard(
        id = dto.id,
        weight = dto.weight,
        sizeHeight = dto.sizeHeight,
        sizeWidth = dto.sizeWidth,
        sizeLength = dto.sizeLength,
        comment = dto.comment,
        flags = dto.flags,
        photos = dto.photos?.map { ProductPhotoItem(it.key, it. value) }.orEmpty(),
    )

    private fun mapDefect(dto: ProductDefectDto): ProductDefect = ProductDefect(
        comment = dto.comment,
        photos = dto.photos?.map { ProductPhotoItem(it.key, it. value) }.orEmpty(),
    )

    private fun mapActions(dto: ProductActionsDto): ProductActions = ProductActions(
        split = dto.split,
        notFound = dto.notFound,
        reprint = dto.reprint,
        defect = dto.defect,
        suspectDefect = dto.suspectDefect,
        writeOff = dto.writeOff
    )
} 