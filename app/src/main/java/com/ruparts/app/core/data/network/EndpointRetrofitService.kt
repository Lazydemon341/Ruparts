package com.ruparts.app.core.data.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ruparts.app.core.data.network.util.fromJson
import com.ruparts.app.core.data.network.util.toJsonObject
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface EndpointRetrofitService {
    @POST("endpoint")
    suspend fun endpoint(
        @Body body: JsonObject,
        @Query("XDEBUG_TRIGGER") debugTrigger: Int = 0,
    ): JsonObject
}

suspend inline fun <reified T : EndpointRequestDto<*>, reified R : Any> EndpointRetrofitService.request(
    body: T,
    gson: Gson,
): R {
    val response = endpoint(
        body = gson.toJsonObject(body)
    )
    return gson.fromJson(response)
}
