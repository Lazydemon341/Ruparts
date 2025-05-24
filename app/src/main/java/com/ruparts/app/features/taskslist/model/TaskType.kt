package com.ruparts.app.features.taskslist.model

enum class TaskType(val displayName: String) {
    CUSTOM("Пользовательская"),
    PURCHASE_ORDER_ARRANGE_DELIVERY("Заказ логистики"),
    PURCHASE_ORDER_GOODS_RECEPTION("Приём груза"),
    PURCHASE_ORDER_GOODS_ACCEPTING("Приходование товара на склад"),
    PURCHASE_ORDER_PROCESS_FLAWS("Разбор брака"),
    PURCHASE_ORDER_PRODUCT_RETURN_ASSEMBLY("Сборка возврата"),
    PURCHASE_DELIVERY_LOGISTICS_CONTROL("Контроль логистики"),
    SELL_ORDER_ASSEMBLY_DELIVERY("Сборка заказа")
}
