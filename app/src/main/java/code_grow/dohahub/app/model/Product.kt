package code_grow.dohahub.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Product(
    @Json(name = "id") val productId: Int,
    val name: String,
    @Json(name = "body") val details: String,
    @Json(name = "mobile") val phoneNumber: String,
    @Json(name = "whatsapp") val whatsApp: String,
    val address: String,
    @Json(name = "image") val productPhoto: String,
    @Json(name = "user_id") val userId: Int,
    val price: Double,
    @Json(name = "store_id") val storeId: Int,
    @Json(name = "city_id") val cityId: Int,
    @Json(name = "user_name") val username: String,
    @Json(name = "user_image") val userPicture: String,
    @Json(name = "store_name") val storeName: String?,
    @Json(name = "city_name") val cityName: String?
) : Parcelable