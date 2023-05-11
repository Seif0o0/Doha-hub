package code_grow.dohahub.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
data class Campaign(
    @Json(name = "id") val campaignId: Int,
    @Json(name = "category_id") val categoryId: Int,
    @Json(name = "user_id") val userId: Int,
    @Json(name = "provider_id") val providerId: Int?,
    @Json(name = "user_name") val username: String,
    @Json(name = "user_image") val userProfilePic: String,
    val title: String,
    var status: Int,
    @Json(name = "body") val details: String,
    @Json(name = "money") val price: String,
    @Json(name = "suggested_price") val suggestedPrice: String,
    @Json(name = "image") val photo: String,
    @Json(name = "category_name") val categoryName: String,
    @Json(name = "selected_date") val selectedDate: String
) : Parcelable