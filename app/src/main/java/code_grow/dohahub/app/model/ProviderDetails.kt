package code_grow.dohahub.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
class ProviderDetails(
    @Json(name = "id") val providerId: Int,
    @Json(name = "name") val username: String,
    @Json(name = "breif") val brief: String,
    val description: String,
    @Json(name = "mobile") val phoneNumber: String,
    @Json(name = "username") val email: String,
    val address: String,
//    @Json(name = "authorized_blog") val s: String,
    @Json(name = "image") val profilePicture: String,
    @Json(name = "role") val userType: Int,/* 1-> user, 2-> provider */
    @Json(name = "facebook") val facebookLink: String,
    @Json(name = "instagram") val instagramLink: String,
    @Json(name = "youtube") val youtubeLink: String,
    @Json(name = "behance") val behanceLink: String,
    val services: MutableList<ProviderService>?,
    @Json(name = "portoflio") val portfolio: MutableList<String>?
) : Parcelable {
    constructor() : this(0, "", "", "", "", "", "", "", 1, "", "", "", "", mutableListOf(), null)
}