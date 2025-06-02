package com.ruparts.app.core.task.library.data.model

import com.ruparts.app.core.data.network.EndpointRequestDto

class TaskLibraryRequest() : EndpointRequestDto<Map<String, String>>(
    action = "app.task.library",
    data = null,
)
