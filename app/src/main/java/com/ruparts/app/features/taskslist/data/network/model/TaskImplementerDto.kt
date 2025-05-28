package com.ruparts.app.features.taskslist.data.network.model

import com.google.gson.annotations.SerializedName
import com.ruparts.app.features.taskslist.model.TaskImplementer

enum class TaskImplementerDto {
    @SerializedName("ROLE_USER")
    USER,
    
    @SerializedName("ROLE_SUPPLIER")
    SUPPLIER,
    
    @SerializedName("ROLE_HeadOfWarehouse")
    HEAD_OF_WAREHOUSE,
    
    @SerializedName("ROLE_Storekeeper")
    STOREKEEPER,
    
    @SerializedName("ROLE_LogisticsControl")
    LOGISTICS_CONTROL,
    
    @SerializedName("ROLE_PurchasesManager")
    PURCHASES_MANAGER,
    
    @SerializedName("ROLE_FlawsProcessingManager")
    FLAWS_PROCESSING_MANAGER
}

fun TaskImplementerDto.toDomain(): TaskImplementer = when (this) {
    TaskImplementerDto.USER -> TaskImplementer.USER
    TaskImplementerDto.SUPPLIER -> TaskImplementer.SUPPLIER
    TaskImplementerDto.HEAD_OF_WAREHOUSE -> TaskImplementer.HEAD_OF_WAREHOUSE
    TaskImplementerDto.STOREKEEPER -> TaskImplementer.STOREKEEPER
    TaskImplementerDto.LOGISTICS_CONTROL -> TaskImplementer.LOGISTICS_CONTROL
    TaskImplementerDto.PURCHASES_MANAGER -> TaskImplementer.PURCHASES_MANAGER
    TaskImplementerDto.FLAWS_PROCESSING_MANAGER -> TaskImplementer.FLAWS_PROCESSING_MANAGER
}

fun TaskImplementer.toDto(): TaskImplementerDto? {
    return when (this) {
        TaskImplementer.USER -> TaskImplementerDto.USER
        TaskImplementer.SUPPLIER -> TaskImplementerDto.SUPPLIER
        TaskImplementer.HEAD_OF_WAREHOUSE -> TaskImplementerDto.HEAD_OF_WAREHOUSE
        TaskImplementer.STOREKEEPER -> TaskImplementerDto.STOREKEEPER
        TaskImplementer.LOGISTICS_CONTROL -> TaskImplementerDto.LOGISTICS_CONTROL
        TaskImplementer.PURCHASES_MANAGER -> TaskImplementerDto.PURCHASES_MANAGER
        TaskImplementer.FLAWS_PROCESSING_MANAGER -> TaskImplementerDto.FLAWS_PROCESSING_MANAGER
        TaskImplementer.UNKNOWN -> null
    }
}