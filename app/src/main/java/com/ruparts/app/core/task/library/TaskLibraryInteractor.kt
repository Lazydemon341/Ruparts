package com.ruparts.app.core.task.library

import com.ruparts.app.core.task.library.data.repository.TaskLibraryRepository
import javax.inject.Inject

class TaskLibraryInteractor @Inject constructor(
    private val repository: TaskLibraryRepository,
) {

    suspend fun getImplementer(implementerKey: String?): String {
        val taskLibrary = repository.loadTaskLibrary()
            .getOrDefault(TaskLibrary(emptyMap()))
        return taskLibrary.implementers[implementerKey].orEmpty()
    }

    suspend fun getImplementers(): Map<String, String> {
        val taskLibrary = repository.loadTaskLibrary()
            .getOrDefault(TaskLibrary(emptyMap()))
        return taskLibrary.implementers
    }
}