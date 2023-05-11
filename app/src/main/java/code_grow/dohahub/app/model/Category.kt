package code_grow.dohahub.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Category(
    @Json(name = "id") val categoryId: Int,
    val name: String,
    val image: String,
    var isClicked: Boolean = false,
) : Parcelable