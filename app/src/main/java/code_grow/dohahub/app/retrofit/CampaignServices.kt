package code_grow.dohahub.app.retrofit

import code_grow.dohahub.app.model.*
import code_grow.dohahub.app.utils.Constants
import okhttp3.MultipartBody
import retrofit2.http.*

interface CampaignServices {

    @GET(Constants.CAMPAIGN.plus(Constants.CAMPAIGNS))
    suspend fun fetchCampaigns(@QueryMap queryMap: Map<String, Int>): ApiResponse<MutableList<Campaign>>

    @GET(Constants.CAMPAIGN.plus(Constants.GET_OFFERS))
    suspend fun fetchOffers(@QueryMap queryMap: Map<String, Int>): ApiResponse<MutableList<CampaignOffer>>

    @GET(Constants.CAMPAIGN.plus(Constants.CAMPAIGN_CATEGORIES))
    suspend fun fetchCategories(): ApiResponse<MutableList<CampaignCategory>>

    @Multipart
    @POST(Constants.CAMPAIGN.plus(Constants.UPLOAD_CAMPAIGN_PHOTO))
    suspend fun uploadImage(@Part body: MultipartBody.Part): UploadFilesResponse<MutableList<String>>

    @GET(Constants.CAMPAIGN.plus(Constants.ADD_CAMPAIGN))
    suspend fun addNewCampaign(@QueryMap queryMap: Map<String, String>): MessageApiResponse

    @GET(Constants.CAMPAIGN.plus(Constants.EDIT_CAMPAIGN))
    suspend fun editCampaign(@QueryMap queryMap: Map<String, String>): MessageApiResponse

    @GET(Constants.CAMPAIGN.plus(Constants.DELETE_CAMPAIGN))
    suspend fun deleteCampaign(@QueryMap queryMap: Map<String, String>): MessageApiResponse

    @GET(Constants.CAMPAIGN.plus(Constants.SEND_OFFER))
    suspend fun sendOffer(@QueryMap queryMap: Map<String, String>): MessageApiResponse

    @GET(Constants.CAMPAIGN.plus(Constants.ACCEPT_OFFER))
    suspend fun acceptOffer(@QueryMap queryMap: Map<String, Int>): MessageApiResponse

}