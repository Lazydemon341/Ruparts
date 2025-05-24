package com.ruparts.app.features.taskslist.data.network.model

import com.google.gson.annotations.SerializedName
import com.ruparts.app.features.taskslist.model.TaskImplementer

enum class TaskImplementerDto {
    @SerializedName("ROLE_USER")
    USER,

    @SerializedName("ROLE_PurchasesManager")
    PURCHASES_MANAGER,

    @SerializedName("ROLE_Storekeeper")
    STOREKEEPER
}

fun TaskImplementerDto.toDomain(): TaskImplementer = when (this) {
    TaskImplementerDto.USER -> TaskImplementer.USER
    TaskImplementerDto.PURCHASES_MANAGER -> TaskImplementer.PURCHASES_MANAGER
    TaskImplementerDto.STOREKEEPER -> TaskImplementer.STOREKEEPER
}

fun TaskImplementer.toDto(): TaskImplementerDto? {
    return when (this) {
        TaskImplementer.USER -> TaskImplementerDto.USER
        TaskImplementer.PURCHASES_MANAGER -> TaskImplementerDto.PURCHASES_MANAGER
        TaskImplementer.STOREKEEPER -> TaskImplementerDto.STOREKEEPER
        TaskImplementer.UNKNOWN -> null
    }
}