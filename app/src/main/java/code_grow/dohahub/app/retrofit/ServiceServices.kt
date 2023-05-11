package code_grow.dohahub.app.retrofit

import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.model.*
import code_grow.dohahub.app.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ServiceServices {

    @GET(Constants.SERVICE.plus(Constants.CATEGORIES))
    suspend fun fetchCategories(): ApiResponse<MutableList<Category>>

    @GET(Constants.SERVICE.plus(Constants.HOME_DATA))
    suspend fun fetchHomeData(): HomeResponse

    @GET(Constants.SERVICE.plus(Constants.PROVIDER_DETAILS))
    suspend fun fetchProviderDetails(@Query("id") providerId: Int): ApiResponse<ProviderDetails>

    @GET(Constants.SERVICE.plus(Constants.PROVIDERS))
    suspend fun fetchProviders(@QueryMap queryMap: Map<String, String>): ApiResponse<DynamicHomeData>

    @GET(Constants.SERVICE.plus(Constants.PROVIDERS))
    suspend fun fetchMyServices(@QueryMap queryMap: Map<String, String>): ApiResponse<DynamicMyServiceData>

    @GET(Constants.SERVICE.plus(Constants.SERVICE_CHECKOUT))
    suspend fun makeServiceCheckout(@QueryMap queryMap: Map<String, String>): MessageApiResponse

    @GET(Constants.SERVICE.plus(Constants.ORDERS))
    suspend fun fetchOrders(@QueryMap queryMap: Map<String, Int>): ApiResponse<MutableList<OrderResponse>>

    @GET(Constants.SERVICE.plus(Constants.ACCEPT_ORDER))
    suspend fun acceptOrder(@QueryMap queryMap: Map<String, Int>): MessageApiResponse

    @GET(Constants.SERVICE.plus(Constants.DELETE_ORDER))
    suspend fun deleteOrder(@QueryMap queryMap: Map<String, Int>): MessageApiResponse

    @GET(Constants.SERVICE.plus(Constants.FINISH_ORDER))
    suspend fun finishOrder(@QueryMap queryMap: Map<String, Int>): MessageApiResponse

    @GET(Constants.SERVICE.plus(Constants.PROVIDER_FEEDBACK))
    suspend fun sendProviderFeedback(@QueryMap queryMap: Map<String, String>): MessageApiResponse

    @GET(Constants.SERVICE.plus(Constants.CUSTOMER_FEEDBACK))
    suspend fun sendCustomerFeedback(@QueryMap queryMap: Map<String, String>): MessageApiResponse

    @GET(Constants.SERVICE.plus(Constants.ADD_SERVICE))
    suspend fun addNewService(@QueryMap queryMap: Map<String, String>): MessageApiResponse

    @GET(Constants.SERVICE.plus(Constants.EDIT_SERVICE))
    suspend fun editService(@QueryMap queryMap: Map<String, String>): MessageApiResponse

    @GET(Constants.SERVICE.plus(Constants.REMOVE_SERVICE))
    suspend fun removeService(
        @Query("id") serviceId: Int,
        @Query("user_id") userId: Int = UserInfo.userId
    ): MessageApiResponse

}