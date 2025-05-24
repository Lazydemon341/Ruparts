package com.ruparts.app.features.taskslist.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class TaskListItem(
    val id: Long,
    val status: TaskStatus,
    val priority: TaskPriority,
    val title: String,
    val description: String,
    val implementer: TaskImplementer,
    val type: TaskType = TaskType.CUSTOM,
    val createdAtDate: LocalDate?,
    val finishAtDate: LocalDate?,
    val updatedAtDate: LocalDate?,
) : Parcelable