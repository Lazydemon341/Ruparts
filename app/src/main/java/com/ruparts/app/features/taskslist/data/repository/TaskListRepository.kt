package com.ruparts.app.features.taskslist.data.repository

import com.google.gson.Gson
import com.ruparts.app.core.data.network.EndpointRetrofitService
import com.ruparts.app.core.data.network.request
import com.ruparts.app.core.task.library.data.repository.TaskLibraryRepository
import com.ruparts.app.core.utils.runCoroutineCatching
import com.ruparts.app.features.taskslist.data.mapper.TaskMapper
import com.ruparts.app.features.taskslist.data.network.model.TaskListRequestDataDto
import com.ruparts.app.features.taskslist.data.network.model.TaskListRequestDto
import com.ruparts.app.features.taskslist.data.network.model.TaskListResponseDto
import com.ruparts.app.features.taskslist.model.TaskListGroup
import com.ruparts.app.features.taskslist.model.TaskListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskListRepository @Inject constructor(
    private val endpointService: EndpointRetrofitService,
    private val gson: Gson,
    private val mapper: TaskMapper,
    private val taskLibraryRepository: TaskLibraryRepository,
) {

    // suspend fun getTaskList(): Result<List<TaskListGroup>> = withContext(Dispatchers.Default) {
    //     runCoroutineCatching {
    //         coroutineScope {
    //             val taskLibrary = async { taskLibraryRepository.loadTaskLibrary() }
    //             val tasks = async {
    //                 val response = endpointService.request<TaskListRequestDto, TaskListResponseDto>(
    //                     body = TaskListRequestDto(
    //                         data = TaskListRequestDataDto(),
    //                     ),
    //                     gson = gson,
    //                 )
    //                 mapper.mapTasks(response.data.list)
    //             }
    //             taskLibrary.await()
    //             tasks.await()
    //         }
    //     }
    // }
    suspend fun getTaskList(): Result<List<TaskListItem>> = withContext(Dispatchers.Default) {
        runCoroutineCatching {
            coroutineScope {
                val taskLibrary = async { taskLibraryRepository.loadTaskLibrary() }
                val tasks = async {
                    val response = endpointService.request<TaskListRequestDto, TaskListResponseDto>(
                        body = TaskListRequestDto(
                            data = TaskListRequestDataDto(),
                        ),
                        gson = gson,
                    )
                    mapper.mapTasks(response.data.list)
                }
                taskLibrary.await()
                tasks.await()
            }
        }
    }
}