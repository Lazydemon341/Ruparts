package com.ruparts.app.core.model

import java.time.LocalDate

class User(
    val uuid: String,
    val userName: String,
    val displayName: String,
    val isActive: Boolean,
    val roles: List<String>,
    val createdAt: LocalDate?,
    val updatedAt: LocalDate?,
)