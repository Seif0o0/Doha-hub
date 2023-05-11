package code_grow.dohahub.app.retrofit

import code_grow.dohahub.app.model.*
import code_grow.dohahub.app.utils.Constants
import okhttp3.MultipartBody
import retrofit2.http.*

interface GlobalServices {

    @GET(Constants.GLOBAL.plus(Constants.REGISTER))
    suspend fun register(@QueryMap queryMap: Map<String, String>): ApiResponse<User>

    @GET(Constants.GLOBAL.plus(Constants.EDIT_PROFILE))
    suspend fun editProfile(@QueryMap queryMap: Map<String, String>): ApiResponse<User>

    @GET(Constants.GLOBAL.plus(Constants.UPDATE_TOKEN))
    suspend fun updateToken(@QueryMap queryMap: Map<String, String>): ApiResponse<User>

    @GET(Constants.GLOBAL.plus(Constants.LOGIN))
    suspend fun login(
        @Query("mobile") phoneNumber: String,
        @Query("password") password: String,
        @Query("token") firebaseToken: String
    ): ApiResponse<User>

    @GET(Constants.GLOBAL.plus(Constants.SOCIAL_CHECK))
    suspend fun socialCheck(
        @Query("username") email: String,
        @Query("social") social: String = ""
    ): SocialCheckResponse

    @GET(Constants.GLOBAL.plus(Constants.FORGET_PASSWORD))
    suspend fun forgetPassword(
        @Query("username") email: String,
    ): PasswordResponse

    @GET(Constants.GLOBAL.plus(Constants.CHANGE_PASSWORD))
    suspend fun changePassword(
        @QueryMap map: Map<String, String>,
    ): PasswordResponse


    @Multipart
    @POST(Constants.GLOBAL.plus(Constants.UPLOAD_IMAGE))
    suspend fun uploadImage(@Part body: MultipartBody.Part): UploadFilesResponse<MutableList<String>>

    @Multipart
    @POST(Constants.GLOBAL.plus(Constants.UPLOAD_USER_IMAGE))
    suspend fun uploadUserImage(@Part body: MultipartBody.Part): UploadFilesResponse<MutableList<String>>

    @GET(Constants.GLOBAL.plus(Constants.CITIES))
    suspend fun fetchCities(): CitiesResponse


}