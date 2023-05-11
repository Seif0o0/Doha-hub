package code_grow.dohahub.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
data class MyService(
    val title:String,
    val name: String,
    @Json(name = "user_id") val providerId: Int,
    @Json(name = "id") val serviceId: Int,
    @Json(name = "category_name") val categoryName: String,
    @Json(name = "category_breif") val categoryBrief: String,
    @Json(name = "category_price") val categoryPrice: Double,
    @Json(name = "image") val profilePicture: String
):Parcelable