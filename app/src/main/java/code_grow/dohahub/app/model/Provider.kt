package code_grow.dohahub.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Provider(
    val name: String,
    @Json(name = "id") val providerId: Int,
    @Json(name = "user_id") val userId: Int?,
    @Json(name = "category_name") val categoryName: String,
    @Json(name = "category_breif") val categoryBrief: String,
    @Json(name = "category_price") val categoryPrice: Double,
    @Json(name = "image") val profilePicture: String
)