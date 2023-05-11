package code_grow.dohahub.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ApiResponse<T>(
    val data: T?,
    val status: Boolean,
    val message: String?,
    @Json(name = "count") val dataSize: Int?
)