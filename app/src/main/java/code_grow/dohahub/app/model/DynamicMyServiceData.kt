package code_grow.dohahub.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class DynamicMyServiceData(
    @Json(name = "title") val category: String,
    @Json(name = "id") val categoryId :Int?,
    @Json(name = "users") val myServices: MutableList<MyService>
)