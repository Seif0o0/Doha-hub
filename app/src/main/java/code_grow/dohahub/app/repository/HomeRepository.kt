package code_grow.dohahub.app.repository

import code_grow.dohahub.app.retrofit.AppApi

class HomeRepository {
    suspend fun fetchHomeData() = AppApi.serviceServices.fetchHomeData()
    suspend fun fetchCategories() = AppApi.serviceServices.fetchCategories()
    suspend fun fetchProviderDetails(providerId: Int) =
        AppApi.serviceServices.fetchProviderDetails(providerId)

    suspend fun fetchProviders(map: Map<String, String>) =
        AppApi.serviceServices.fetchProviders(map)

    suspend fun makeServiceCheckout(map: Map<String, String>) =
        AppApi.serviceServices.makeServiceCheckout(map)
}