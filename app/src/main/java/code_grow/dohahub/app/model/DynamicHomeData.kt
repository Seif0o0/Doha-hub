package code_grow.dohahub.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class DynamicHomeData(
    @Json(name = "title") val category: String,
    @Json(name = "id") val categoryId :Int?,
    @Json(name = "users") val providers: MutableList<Provider>
)