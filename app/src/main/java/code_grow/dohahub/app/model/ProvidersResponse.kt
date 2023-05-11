package code_grow.dohahub.app.model

import com.squareup.moshi.JsonClass

/*
"data":{
      "title":"Videographer",
      "users"
      status"
 */
@JsonClass(generateAdapter = true)
class ProvidersResponse(
    val data: DynamicHomeData,
    val status: Boolean,
    val message: String?
)