package com.ruparts.app.core.task.library.data.repository

import com.google.gson.Gson
import com.ruparts.app.core.data.network.EndpointRetrofitService
import com.ruparts.app.core.data.network.request
import com.ruparts.app.core.task.library.TaskLibrary
import com.ruparts.app.core.task.library.data.TaskLibraryInMemoryCache
import com.ruparts.app.core.task.library.data.model.TaskLibraryRequest
import com.ruparts.app.core.task.library.data.model.TaskLibraryResponse
import com.ruparts.app.core.utils.runCoroutineCatching
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskLibraryRepository @Inject constructor(
    private val endpointRetrofitService: EndpointRetrofitService,
    private val gson: Gson,
    private val taskLibraryInMemoryCache: TaskLibraryInMemoryCache,
) {

    suspend fun loadTaskLibrary(): Result<TaskLibrary> = withContext(Dispatchers.Default) {
        runCoroutineCatching {
            taskLibraryInMemoryCache.getTaskLibrary()?.let { cachedTaskLibrary ->
                return@runCoroutineCatching cachedTaskLibrary
            }

            val response: TaskLibraryResponse = endpointRetrofitService.request(
                body = TaskLibraryRequest(),
                gson = gson,
            )
            val responseData = response.data ?: error("empty data in TaskLibraryResponse")

            val taskLibrary = TaskLibrary(
                implementers = responseData.implementer.orEmpty()
            )
            taskLibraryInMemoryCache.setTaskLibrary(taskLibrary)

            return@runCoroutineCatching taskLibrary
        }
    }
}
