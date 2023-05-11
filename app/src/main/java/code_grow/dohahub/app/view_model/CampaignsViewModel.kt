package code_grow.dohahub.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.dohahub.app.R
import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.model.Campaign
import code_grow.dohahub.app.model.OrderResponse
import code_grow.dohahub.app.repository.CampaignsRepository
import code_grow.dohahub.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException

private const val TAG = "CampaignsViewModel"

/*
* status = 0 => suggested price , keep buttons
* status = 1 => money , change buttons to send message */
class CampaignsViewModel(
    private val isMyCampaigns: Boolean,
    val application: Application,
    val repo: CampaignsRepository
) : ViewModel() {
    private val _startRequestCampaigns = MutableLiveData(false)
    val startRequestCampaigns: LiveData<Boolean> get() = _startRequestCampaigns

    private val _campaignsResponse = MutableLiveData<Resource>(Resource.Idle)
    val campaignsResponse: LiveData<Resource> get() = _campaignsResponse


    fun startRequestCampaigns(value: Boolean) {
        _startRequestCampaigns.value = value
    }

    init {
        startRequestCampaigns(true)
    }

    fun getCampaigns() {
        val map = mutableMapOf<String, Int>()
        if (isMyCampaigns)
            map["user_id"] = UserInfo.userId
//        else/* client campaigns */
//            map["user_id"] = UserInfo.userId

        viewModelScope.launch {
            try {
                _campaignsResponse.value = Resource.Loading
                val apiResponse = repo.fetchCampaigns(map)
                if (apiResponse.status) {
                    val data = apiResponse.data as MutableList<Campaign>
                    if (data.isEmpty())
                        _campaignsResponse.value = Resource.Empty
                    else
                        _campaignsResponse.value = Resource.Success(data)
                } else {
                    _campaignsResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Campaigns-Req. Failed: $e")
                _campaignsResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException, is SocketTimeoutException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

}