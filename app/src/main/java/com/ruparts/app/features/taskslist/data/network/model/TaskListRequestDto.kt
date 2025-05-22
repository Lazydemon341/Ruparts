package com.ruparts.app.features.taskslist.data.network.model

import com.google.gson.annotations.SerializedName
import com.ruparts.app.core.data.network.EndpointRequestDto
import java.util.Date

class TaskListRequestDto(
    data: TaskListRequestDataDto
) : EndpointRequestDto<TaskListRequestDataDto>(
    action = "app.task.list",
    data = data,
)

class TaskListRequestDataDto(
    @SerializedName("_filter")
    val filter: TaskFilter = TaskFilter(),
    @SerializedName("_pagination")
    val pagination: Pagination = Pagination(),
    @SerializedName("_sorting")
    val sorting: Sorting = Sorting(),
) {

    class Sorting(
        @SerializedName("field")
        val field: String? = null,
        @SerializedName("direction")
        val direction: String? = null
    )

    class Pagination(
        @SerializedName("per_page")
        val perPage: Int? = 100,
        @SerializedName("page")
        val page: Int? = 1
    )

    class TaskFilter(
        @SerializedName("search")
        val search: String? = null,

        @SerializedName("author_id")
        val authorId: String? = null,

        @SerializedName("status")
        val status: List<String?>? = null,

        @SerializedName("type")
        val type: String? = null,

        @SerializedName("implementer")
        val implementer: String? = null,

        @SerializedName("finish_date_before")
        val finishDateBefore: Date? = null,

        @SerializedName("finish_date_after")
        val finishDateAfter: Date? = null,
    )
}