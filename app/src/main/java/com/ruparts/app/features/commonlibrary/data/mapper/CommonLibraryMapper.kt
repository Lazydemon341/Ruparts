package com.ruparts.app.features.commonlibrary.data.mapper

import com.ruparts.app.features.commonlibrary.CommonLibraryData
import com.ruparts.app.features.commonlibrary.ProductFlag
import com.ruparts.app.features.commonlibrary.data.network.model.GetLibraryResponseDataDto
import com.ruparts.app.features.commonlibrary.data.network.model.WmsProductFlagDto
import javax.inject.Inject

class CommonLibraryMapper @Inject constructor() {

    fun mapCommonLibrary(dto: GetLibraryResponseDataDto?): CommonLibraryData? {
        if (dto == null) {
            return null
        }

        return CommonLibraryData(
            productFlags = dto.context?.wms?.wmsProductFlag?.associateBy { it.id }
                ?.mapValues { mapWmsProductFlag(it.value) }
                .orEmpty()
        )
    }

    private fun mapWmsProductFlag(dto: WmsProductFlagDto): ProductFlag {
        return ProductFlag(
            id = dto.id,
            title = dto.title,
            category = dto.category.name,
        )
    }
}
