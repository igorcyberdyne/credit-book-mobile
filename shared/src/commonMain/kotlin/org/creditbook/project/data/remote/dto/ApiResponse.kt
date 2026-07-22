package org.creditbook.project.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ApiResponse<T>(
    val data: T,
    val meta: JsonElement? = null,
    val message: String? = null
)