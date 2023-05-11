package code_grow.dohahub.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Comment(
    @Json(name = "id") val commentId: Int,
    @Json(name = "user_id") val userId: Int,
    val comment: String,
    @Json(name = "created") val createdAt: String,/* 2021-12-28 */
    @Json(name = "user_name") val username: String?,
    @Json(name = "user_image") val userImage: String
):Parcelable
