package code_grow.dohahub.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "id") val userId: Int,
    @Json(name = "name") val username: String,
    @Json(name = "breif") val brief: String,
    @Json(name = "description") val description: String,
    @Json(name = "password") val password: String,
    @Json(name = "username") val email: String,
    val address: String,
    @Json(name = "mobile") val phoneNumber: String,
    @Json(name = "image") val profilePicture: String,
    @Json(name = "token") val firebaseToken: String?,
    @Json(name = "facebook") val facebookLink: String,
    @Json(name = "instagram") val instagramLink: String,
    @Json(name = "youtube") val youtubeLink: String,
    @Json(name = "behance") val behanceLink: String,
    @Json(name = "role") val userType: Int,/* 1-> user, 2-> provider */
//    @Json(name = "authorized_blog") val blogsCount: Int,
    @Json(name = "verify") val isVerified: Int,
)