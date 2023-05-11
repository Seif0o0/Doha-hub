package code_grow.dohahub.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.dohahub.app.R
import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.model.CampaignOffer
import code_grow.dohahub.app.repository.CampaignsRepository
import code_grow.dohahub.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException

private const val TAG = "CampaignOffersViewModel"

class CampaignOffersViewModel(
    private val campaignId: Int,
//    private val providerId: Int,//offer owner
//    private val userId: Int,//campaign owner
    private val application: Application,
    private val repo: CampaignsRepository
) : ViewModel() {
    private val _startRequestOffers = MutableLiveData(false)
    val startRequestOffers: LiveData<Boolean> get() = _startRequestOffers

    private val _offersResponse = MutableLiveData<Resource>(Resource.Idle)
    val offersResponse: LiveData<Resource> get() = _offersResponse


    fun startRequestOffers(value: Boolean) {
        _startRequestOffers.value = value
    }

    private val _startAcceptOffer = MutableLiveData(false)
    val startAcceptOffer: LiveData<Boolean> get() = _startAcceptOffer

    private val _acceptOfferResponse = MutableLiveData<Resource>(Resource.Idle)
    val acceptOfferResponse: LiveData<Resource> get() = _acceptOfferResponse


    private var offerId = 0
    private var providerId = 0
    fun startAcceptOffer(value: Boolean, offerId: Int? = null, providerId: Int?=null) {
        this.offerId = offerId ?: 0
        this.providerId = providerId ?: 0
        _startAcceptOffer.value = value
    }

    init {
        startRequestOffers(true)
    }

    val loadingProgress = MutableLiveData<Resource>(Resource.Idle)

    fun getOffers() {
        val map = mutableMapOf<String, Int>()
        map["campaign_id"] = campaignId
        map["user_id"] = UserInfo.userId

        viewModelScope.launch {
            try {
                loadingProgress.value = Resource.Loading
                _offersResponse.value = Resource.Loading
                val apiResponse = repo.fetchOffers(map)
                if (apiResponse.status) {
                    val data = apiResponse.data as MutableList<CampaignOffer>
                    if (data.isEmpty())
                        _offersResponse.value = Resource.Empty
                    else
                        _offersResponse.value = Resource.Success(data)
                } else {
                    _offersResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Campaigns-Req. Failed: $e")
                _offersResponse.value = Resource.Failed(catchNetworkException(e))
            } finally {
                loadingProgress.value = Resource.Idle
            }
        }
    }

    fun acceptOffer() {
        viewModelScope.launch {
            val map = mutableMapOf<String, Int>()
            map["id"] = offerId
            map["provider_id"] = providerId
            map["user_id"] = UserInfo.userId

            try {
                loadingProgress.value = Resource.Loading
                _acceptOfferResponse.value = Resource.Loading
                val apiResponse = repo.acceptOffer(map)
                if (apiResponse.status) {
                    _acceptOfferResponse.value = Resource.Success(apiResponse.message)
                } else {
                    _acceptOfferResponse.value = Resource.Failed(apiResponse.message)
                }
            } catch (e: Exception) {
                Log.d(TAG, "AcceptOffer-Req. Failed: $e")
                _acceptOfferResponse.value = Resource.Failed(catchNetworkException(e))
            } finally {
                loadingProgress.value = Resource.Idle
            }
        }
    }


    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException, is SocketTimeoutException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }
}