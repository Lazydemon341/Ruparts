package com.ruparts.app.features.taskslist.data.network.model

import com.google.gson.annotations.SerializedName

class AuthorDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("username")
    val username: String
)