package com.ruparts.app.core.task.library.data

import com.ruparts.app.core.task.library.TaskLibrary
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskLibraryInMemoryCache @Inject constructor() {

    @Volatile
    private var taskLibrary: TaskLibrary? = null
    private val mutex = Mutex()

    suspend fun getTaskLibrary(): TaskLibrary? = mutex.withLock {
        taskLibrary
    }

    suspend fun setTaskLibrary(taskLibrary: TaskLibrary) = mutex.withLock {
        this.taskLibrary = taskLibrary
    }
}