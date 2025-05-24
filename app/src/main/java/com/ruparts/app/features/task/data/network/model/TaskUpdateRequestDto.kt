package com.ruparts.app.features.task.data.network.model

import com.google.gson.annotations.SerializedName
import com.ruparts.app.core.data.network.EndpointRequestDto
import com.ruparts.app.features.taskslist.data.network.model.TaskImplementerDto
import com.ruparts.app.features.taskslist.data.network.model.TaskPriorityDto
import com.ruparts.app.features.taskslist.model.TaskImplementer
import com.ruparts.app.features.taskslist.model.TaskListItem
import com.ruparts.app.features.taskslist.model.TaskPriority
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

    @SerializedName("priority")
    val priority: TaskPriorityDto?,

    @SerializedName("finish_at")
    val finishAt: String?,

    @SerializedName("implementer")
    val implementer: TaskImplementerDto?
) {
    companion object {
        fun fromTaskListItem(task: TaskListItem): TaskUpdateRequestDataDto {
            val priorityString = when (task.priority) {
                TaskPriority.HIGH -> TaskPriorityDto.HIGH
                TaskPriority.MEDIUM -> TaskPriorityDto.MEDIUM
                TaskPriority.LOW -> TaskPriorityDto.LOW
            }

            val implementerString = when (task.implementer) {
                TaskImplementer.USER -> TaskImplementerDto.USER
                TaskImplementer.PURCHASES_MANAGER -> TaskImplementerDto.PURCHASES_MANAGER
                TaskImplementer.STOREKEEPER -> TaskImplementerDto.STOREKEEPER
                TaskImplementer.UNKNOWN -> null
            }

            val formattedFinishDate = task.finishAtDate?.let { date ->
                DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date)
            }

            return TaskUpdateRequestDataDto(
                id = task.id,
                title = task.title,
                description = task.description,
                priority = priorityString,
                implementer = implementerString,
                finishAt = formattedFinishDate,
            )
        }
    }
}
