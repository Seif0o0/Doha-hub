package code_grow.dohahub.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
data class ProviderService(
    @Json(name = "service_id") val serviceId: Int?,
    @Json(name = "id") val categoryId: Int,
    @Json(name = "service_name") val jobTitle: String?,
    @Json(name = "title") val ServiceTitle: String,
    @Json(name = "breif") val details: String,
    val price: Double,
    var isClicked: Boolean = false
) : Parcelable