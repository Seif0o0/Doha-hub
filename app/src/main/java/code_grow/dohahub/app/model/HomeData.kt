package code_grow.dohahub.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class HomeData(
    @Json(name = "categories") val dynamicData: MutableList<DynamicHomeData>?,
    @Json(name = "featured_users") val featuredList: MutableList<Provider>
)