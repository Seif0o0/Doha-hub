package code_grow.dohahub.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductCategory(
    @Json(name = "id") val categoryId: Int,
    val name: String,
    var isClicked: Boolean = false
)