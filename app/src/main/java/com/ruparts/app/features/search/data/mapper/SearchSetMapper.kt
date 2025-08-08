package com.ruparts.app.features.search.data.mapper

import com.ruparts.app.core.utils.toLocalDate
import com.ruparts.app.features.search.data.network.model.SearchSetItemDto
import com.ruparts.app.features.search.presentation.SearchScreenSearchSet
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class SearchSetMapper @Inject constructor() {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss xxx")

    fun mapSearchSets(list: List<SearchSetItemDto>): List<SearchScreenSearchSet> {
        return list.map { item ->
            mapTask(item, dateFormatter)
        }
    }

    private fun mapTask(item: SearchSetItemDto, dateFormatter: DateTimeFormatter): SearchScreenSearchSet {
        return SearchScreenSearchSet(
            id = item.id,
            text = item.title,
            supportingText = "ID " + item.id + ", " + item.author + ", "+ item.created.toLocalDate(dateFormatter),
            checked = false
            // title = item.title,
            // author = item.author,
            // created = item.created.toLocalDate(dateFormatter),
        )
    }
}
