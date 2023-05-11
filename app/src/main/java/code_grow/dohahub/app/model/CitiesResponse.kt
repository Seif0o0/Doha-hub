package code_grow.dohahub.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class CitiesResponse(
    @Json(name = "cities") val cities: MutableList<City>,
    val status: Boolean,
    val message: String?,
    @Json(name = "count") val listSize: Int?
)