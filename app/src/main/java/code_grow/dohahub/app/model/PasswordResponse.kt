package code_grow.dohahub.app.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PasswordResponse (
    val status: Boolean,
    val message: String?
)