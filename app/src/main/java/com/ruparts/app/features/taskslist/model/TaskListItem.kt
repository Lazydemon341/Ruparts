package com.ruparts.app.features.taskslist.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskListItem(
    val id: Long,
    val status: TaskStatus,
    val priority: TaskPriority,
    val title: String,
    val description: String,
    val implementer: TaskImplementer,
    val createdAtDate: String?,
    val finishAtDate: String?
) : Parcelable