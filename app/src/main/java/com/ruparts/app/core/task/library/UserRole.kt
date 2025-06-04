package com.ruparts.app.core.task.library

enum class UserRole constructor(val key: String) {
    USER("ROLE_USER"),
    SUPPLIER("ROLE_SUPPLIER"),
    HEAD_OF_WAREHOUSE("ROLE_HeadOfWarehouse"),
    STOREKEEPER("ROLE_Storekeeper"),
    LOGISTICS_CONTROL("ROLE_LogisticsControl"),
    PURCHASES_MANAGER("ROLE_PurchasesManager"),
    FLAWS_PROCESSING_MANAGER("ROLE_FlawsProcessingManager"),
}