package code_grow.dohahub.app.repository

import code_grow.dohahub.app.retrofit.AppApi

class OrdersRepository {
    suspend fun fetchOrders(map: Map<String, Int>) = AppApi.serviceServices.fetchOrders(map)
    suspend fun acceptOrder(map: Map<String, Int>) = AppApi.serviceServices.acceptOrder(map)
    suspend fun finishOrder(map: Map<String, Int>) = AppApi.serviceServices.finishOrder(map)
    suspend fun deleteOrder(map: Map<String, Int>) = AppApi.serviceServices.deleteOrder(map)
    suspend fun sendProviderFeedback(map: Map<String, String>) =
        AppApi.serviceServices.sendProviderFeedback(map)

    suspend fun sendCustomerFeedback(map: Map<String, String>) =
        AppApi.serviceServices.sendCustomerFeedback(map)
}