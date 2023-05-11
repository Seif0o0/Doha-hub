package code_grow.dohahub.app.repository

import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.retrofit.AppApi

class MyServicesRepository {
    suspend fun fetchMyServices() =
        AppApi.serviceServices.fetchMyServices(mutableMapOf("user_id" to UserInfo.userId.toString()))

    suspend fun fetchCategories() = AppApi.serviceServices.fetchCategories()
    suspend fun addNewService(map: Map<String, String>) = AppApi.serviceServices.addNewService(map)
    suspend fun editService(map: Map<String, String>) = AppApi.serviceServices.editService(map)
    suspend fun removeService(serviceId: Int) =
        AppApi.serviceServices.removeService(serviceId = serviceId)
}