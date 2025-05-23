package com.ruparts.app.core.data.network.util

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

inline fun <reified T> Gson.fromJson(json: JsonElement): T {
    return fromJson(json, object : TypeToken<T>() {})
}

inline fun <reified T> Gson.fromJson(json: String): T {
    return fromJson(json, object : TypeToken<T>() {})
}

fun <T> Gson.toJsonObject(obj: T): JsonObject {
    val jsonString = toJson(obj)
    return fromJson<JsonObject>(jsonString)
}