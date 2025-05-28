package com.ruparts.app.features.task.data.network.model

import com.google.gson.annotations.SerializedName
import com.ruparts.app.core.data.network.EndpointRequestDto
import com.ruparts.app.features.taskslist.data.network.model.TaskStatusDto
import com.ruparts.app.features.taskslist.data.network.model.toDto
import com.ruparts.app.features.taskslist.model.TaskStatus

class TaskChangeStatusRequestDto(
    data: TaskChangeStatusRequestDataDto
) : EndpointRequestDto<TaskChangeStatusRequestDataDto>(
    action = "app.task.change_status",
    data = data,
)

class TaskChangeStatusRequestDataDto(
    @SerializedName("id")
    val id: Long?,

    @SerializedName("status")
    val status: TaskStatusDto?
)

{
    companion object {
        fun fromTask(id: Long, newStatus: TaskStatus): TaskChangeStatusRequestDataDto {
            return TaskChangeStatusRequestDataDto(
                id = id,
                status = newStatus.toDto()
            )
        }
    }
}
