package code_grow.dohahub.app.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
data class Order(
    @Json(name = "id") val orderId: Int,
    val price: Double,
    val status: Int,
    @Json(name = "user_id") val userId: Int,
    @Json(name = "customer_id") val customerId: Int,
    @Json(name = "created") val date: String
) : Parcelable
