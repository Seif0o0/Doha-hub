package code_grow.dohahub.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class OrderResponse(
    @Json(name = "Order") val orderDetails: Order,
    @Json(name = "category_name") val categoryName: String?,
    @Json(name = "service_name") val serviceName: String?,
    @Json(name = "name") val providerName: String?,
    @Json(name = "image") val providerImage: String?,
    @Json(name = "User") val provider: ProviderDetails
) : Parcelable