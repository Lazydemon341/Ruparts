package com.ruparts.app.features.taskslist.data.network.model

import com.google.gson.annotations.SerializedName
import com.ruparts.app.features.taskslist.model.TaskType

enum class TaskTypeDto {
    @SerializedName("custom")
    CUSTOM,

    @SerializedName("purchase_order_arrange_delivery")
    PURCHASE_ORDER_ARRANGE_DELIVERY,

    @SerializedName("purchase_order_goods_reception")
    PURCHASE_ORDER_GOODS_RECEPTION,

    @SerializedName("purchase_order_goods_accepting")
    PURCHASE_ORDER_GOODS_ACCEPTING,

    @SerializedName("purchase_order_process_flaws")
    PURCHASE_ORDER_PROCESS_FLAWS,

    @SerializedName("purchase_order_product_return_assembly")
    PURCHASE_ORDER_PRODUCT_RETURN_ASSEMBLY,

    @SerializedName("purchase_delivery_logistics_control")
    PURCHASE_DELIVERY_LOGISTICS_CONTROL,

    @SerializedName("sell_order_assembly_delivery")
    SELL_ORDER_ASSEMBLY_DELIVERY
}

fun TaskTypeDto.toDomain(): TaskType = when (this) {
    TaskTypeDto.CUSTOM -> TaskType.CUSTOM
    TaskTypeDto.PURCHASE_ORDER_ARRANGE_DELIVERY -> TaskType.PURCHASE_ORDER_ARRANGE_DELIVERY
    TaskTypeDto.PURCHASE_ORDER_GOODS_RECEPTION -> TaskType.PURCHASE_ORDER_GOODS_RECEPTION
    TaskTypeDto.PURCHASE_ORDER_GOODS_ACCEPTING -> TaskType.PURCHASE_ORDER_GOODS_ACCEPTING
    TaskTypeDto.PURCHASE_ORDER_PROCESS_FLAWS -> TaskType.PURCHASE_ORDER_PROCESS_FLAWS
    TaskTypeDto.PURCHASE_ORDER_PRODUCT_RETURN_ASSEMBLY -> TaskType.PURCHASE_ORDER_PRODUCT_RETURN_ASSEMBLY
    TaskTypeDto.PURCHASE_DELIVERY_LOGISTICS_CONTROL -> TaskType.PURCHASE_DELIVERY_LOGISTICS_CONTROL
    TaskTypeDto.SELL_ORDER_ASSEMBLY_DELIVERY -> TaskType.SELL_ORDER_ASSEMBLY_DELIVERY
}

fun TaskType.toDto(): TaskTypeDto = when (this) {
    TaskType.CUSTOM -> TaskTypeDto.CUSTOM
    TaskType.PURCHASE_ORDER_ARRANGE_DELIVERY -> TaskTypeDto.PURCHASE_ORDER_ARRANGE_DELIVERY
    TaskType.PURCHASE_ORDER_GOODS_RECEPTION -> TaskTypeDto.PURCHASE_ORDER_GOODS_RECEPTION
    TaskType.PURCHASE_ORDER_GOODS_ACCEPTING -> TaskTypeDto.PURCHASE_ORDER_GOODS_ACCEPTING
    TaskType.PURCHASE_ORDER_PROCESS_FLAWS -> TaskTypeDto.PURCHASE_ORDER_PROCESS_FLAWS
    TaskType.PURCHASE_ORDER_PRODUCT_RETURN_ASSEMBLY -> TaskTypeDto.PURCHASE_ORDER_PRODUCT_RETURN_ASSEMBLY
    TaskType.PURCHASE_DELIVERY_LOGISTICS_CONTROL -> TaskTypeDto.PURCHASE_DELIVERY_LOGISTICS_CONTROL
    TaskType.SELL_ORDER_ASSEMBLY_DELIVERY -> TaskTypeDto.SELL_ORDER_ASSEMBLY_DELIVERY
}
