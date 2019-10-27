package models

import com.beust.klaxon.Json

data class VKResponse(val response: Response)

data class Response (
    @Json(name = "total_count")
    val totalCount: Int
)