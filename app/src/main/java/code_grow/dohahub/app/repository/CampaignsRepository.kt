package code_grow.dohahub.app.repository

import code_grow.dohahub.app.retrofit.AppApi
import okhttp3.MultipartBody

class CampaignsRepository {
    suspend fun fetchCampaigns(map: Map<String, Int>) = AppApi.campaignServices.fetchCampaigns(map)
    suspend fun fetchOffers(map: Map<String, Int>) = AppApi.campaignServices.fetchOffers(map)
    suspend fun fetchCategories() = AppApi.campaignServices.fetchCategories()
    suspend fun uploadImage(body: MultipartBody.Part) = AppApi.campaignServices.uploadImage(body)
    suspend fun addNewCampaign(map: Map<String, String>) =
        AppApi.campaignServices.addNewCampaign(map)

    suspend fun editCampaign(map: Map<String, String>) = AppApi.campaignServices.editCampaign(map)
    suspend fun deleteCampaign(map: Map<String, String>) =
        AppApi.campaignServices.deleteCampaign(map)

    suspend fun sendOffer(map: Map<String, String>) = AppApi.campaignServices.sendOffer(map)
    suspend fun acceptOffer(map: Map<String, Int>) = AppApi.campaignServices.acceptOffer(map)

}