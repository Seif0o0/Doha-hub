package code_grow.dohahub.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Message(
    @Json(name = "sender_name") val senderName: String,
    @Json(name = "sender_id") val senderId: Int,
    @Json(name = "sender_image") val senderImage: String,
    @Json(name = "reciever_name") val receiverName: String,
    @Json(name = "reciever_id") val receiverId: Int,
    @Json(name = "reciever_image") val receiverImage: String,
    val message: String,
    val date: String
)