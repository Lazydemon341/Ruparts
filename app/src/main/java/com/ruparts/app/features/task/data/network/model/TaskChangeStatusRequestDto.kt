package com.ruparts.app.features.task.data.network.model

import com.ruparts.app.core.data.network.EndpointRequestDto

class TaskChangeStatusRequestDto(
    data: TaskChangeStatusRequestDataDto
) : EndpointRequestDto<TaskChangeStatusRequestDataDto>(
    action = "app.task.change_status",
    data = data,
)

class TaskChangeStatusRequestDataDto(
    // TODO: реализовать модель запроса app.task.change_status
    //  (описать поля — id: Long и status: TaskStatusDto)
)