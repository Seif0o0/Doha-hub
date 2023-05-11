package code_grow.dohahub.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class CampaignOffer(
    @Json(name = "id") val offerId: Int,
    @Json(name = "provider_id") val providerId: Int,
    @Json(name = "campaign_id") val campaignId: Int,
    @Json(name = "user_id") val userId: Int,
    val status: Int,
    val price: Double,
    @Json(name = "body") val details: String,
    @Json(name = "user_name") val username: String,
    @Json(name = "user_image") val userPicture: String,
    @Json(name = "provider_name") val providerName: String,
)