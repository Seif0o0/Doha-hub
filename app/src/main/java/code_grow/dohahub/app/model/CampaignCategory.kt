package code_grow.dohahub.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class CampaignCategory (
    @Json(name = "id") val categoryId:Int,
    val name:String
)