package code_grow.dohahub.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
data class Article(
    @Json(name = "id") val articleId: Int,
    @Json(name = "user_id") val userId: Int,
    val title: String,
    val body: String,
    @Json(name = "image") val photo: String,
    @Json(name = "views") val views: Int,
    @Json(name = "user_name") val username: String?,
    @Json(name = "user_image") val userPicture: String,
    @Json(name = "webview") val isWebView: Boolean,
    val comments : MutableList<Comment>
) : Parcelable