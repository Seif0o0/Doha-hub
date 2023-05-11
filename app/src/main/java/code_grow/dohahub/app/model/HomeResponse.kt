package code_grow.dohahub.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class HomeResponse(
    @Json(name = "data") val homeData: HomeData?,
    val status: Boolean,
    val message: String?
)