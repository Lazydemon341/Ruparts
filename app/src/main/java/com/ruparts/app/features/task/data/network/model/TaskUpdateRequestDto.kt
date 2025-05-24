package com.ruparts.app.features.task.data.network.model

import com.google.gson.annotations.SerializedName
import com.ruparts.app.core.data.network.EndpointRequestDto
import com.ruparts.app.features.taskslist.data.network.model.TaskImplementerDto
import com.ruparts.app.features.taskslist.data.network.model.TaskPriorityDto
import com.ruparts.app.features.taskslist.data.network.model.TaskTypeDto
import com.ruparts.app.features.taskslist.data.network.model.toDto
import com.ruparts.app.features.taskslist.model.TaskListItem
import java.time.format.DateTimeFormatter

class TaskUpdateRequestDto(
    data: TaskUpdateRequestDataDto
) : EndpointRequestDto<TaskUpdateRequestDataDto>(
    action = "app.task.update",
    data = data,
)

class TaskUpdateRequestDataDto(
    @SerializedName("id")
    val id: Long?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("type")
    val type: TaskTypeDto?,

    @SerializedName("priority")
    val priority: TaskPriorityDto?,

    @SerializedName("finish_at")
    val finishAt: String?,

    @SerializedName("implementer")
    val implementer: TaskImplementerDto?
) {
    companion object {
        fun fromTask(task: TaskListItem): TaskUpdateRequestDataDto {
            val formattedFinishDate = task.finishAtDate?.let { date ->
                DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date)
            }
            return TaskUpdateRequestDataDto(
                id = task.id,
                title = task.title,
                description = task.description,
                type = task.type.toDto(),
                priority = task.priority.toDto(),
                implementer = task.implementer.toDto(),
                finishAt = formattedFinishDate,
            )
        }
    }
}
