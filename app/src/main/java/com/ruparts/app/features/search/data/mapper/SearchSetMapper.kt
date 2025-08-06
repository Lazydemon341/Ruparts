package com.ruparts.app.features.search.data.mapper

import com.ruparts.app.core.utils.toLocalDate
import com.ruparts.app.features.search.data.network.model.SearchSetItemDto
import com.ruparts.app.features.search.model.SearchSetItem
import com.ruparts.app.features.taskslist.data.network.model.toDomain
import com.ruparts.app.features.taskslist.model.TaskListItem
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class SearchSetMapper @Inject constructor() {

    private val dateFormatterLazy = lazy {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss xxx")
    }

    fun mapSearchSets(list: List<SearchSetItemDto>): List<SearchSetItem> {
        val dateFormatter = dateFormatterLazy.value
        return list.map { item ->
            mapTask(item, dateFormatter)
        }
    }


    private fun mapTask(item: SearchSetItemDto, dateFormatter: DateTimeFormatter): SearchSetItem {
        return SearchSetItem(
            id = item.id,
            title = item.title,
            author = item.author,
            created = item.created.toLocalDate(dateFormatter),
        )
    }
}