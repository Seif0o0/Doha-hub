package code_grow.dohahub.app.repository

import code_grow.dohahub.app.retrofit.AppApi
import okhttp3.MultipartBody

class AuthRepository {
    suspend fun login(phoneNumber: String, password: String, firebaseToken: String) =
        AppApi.globalServices.login(phoneNumber, password, firebaseToken)

    suspend fun socialCheck(email: String) = AppApi.globalServices.socialCheck(email = email)

    suspend fun register(map: Map<String, String>) = AppApi.globalServices.register(map)

    suspend fun updateToken(map: Map<String, String>) = AppApi.globalServices.updateToken(map)

    suspend fun forgetPassword(email: String) = AppApi.globalServices.forgetPassword(email)

    suspend fun changePassword(map: Map<String, String>) = AppApi.globalServices.changePassword(map)

    suspend fun editProfile(map: Map<String, String>) = AppApi.globalServices.editProfile(map)

    suspend fun uploadImage(body: MultipartBody.Part) = AppApi.globalServices.uploadImage(body)

    suspend fun uploadUserImage(body: MultipartBody.Part) =
        AppApi.globalServices.uploadUserImage(body)
}