package code_grow.dohahub.app.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SocialCheckResponse(
    val status: Boolean,
    val data: User?,
    val message: String?,
    val signup: Boolean?
)