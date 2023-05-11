package code_grow.dohahub.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class UploadFilesResponse<T>(
    @Json(name = "images") val data: T?,
    val message: String?
)