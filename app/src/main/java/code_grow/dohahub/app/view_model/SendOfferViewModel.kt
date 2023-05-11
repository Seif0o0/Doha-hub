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
import code_grow.dohahub.app.repository.CampaignsRepository
import code_grow.dohahub.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException

private const val TAG = "SendOfferViewModel"

class SendOfferViewModel(
    private val campaign: Campaign,
    private val application: Application,
    private val repo: CampaignsRepository
) : ViewModel() {
    /* details input */
    val detailsLiveData = MutableLiveData("")

    /* details error message */
    val detailsErrorLiveData = MutableLiveData("")

    /* price input */
    val priceLiveData = MutableLiveData("")

    /* price error message */
    val priceErrorLiveData = MutableLiveData("")

    private val _startSendingOffer = MutableLiveData(false)
    val startSendingOffer: LiveData<Boolean> get() = _startSendingOffer

    fun startSendingOffer(value: Boolean) {
        _startSendingOffer.value = value
    }

    private val _sendOfferResponse = MutableLiveData<Resource>(Resource.Idle)
    val sendOfferResponse: LiveData<Resource> get() = _sendOfferResponse

    fun submitBtnClicked() {
        var pass = true
        if (!validateDetails(detailsLiveData.value!!))
            pass = false
        if (!validatePrice(priceLiveData.value!!))
            pass = false

        if (pass)
            startSendingOffer(true)
    }

    fun sendOffer() {
        viewModelScope.launch {
            val map = mutableMapOf<String, String>()

            map["user_id"] = campaign.userId.toString()
            map["body"] = detailsLiveData.value!!
            map["price"] = priceLiveData.value!!
            map["campaign_id"] = campaign.campaignId.toString()
            map["provider_id"] = UserInfo.userId.toString()

            try {
                _sendOfferResponse.value = Resource.Loading
                val apiResponse = repo.sendOffer(map)
                if (apiResponse.status) {
                    _sendOfferResponse.value = Resource.Success(apiResponse.message)
                } else {
                    _sendOfferResponse.value = Resource.Failed(apiResponse.message)
                }
            } catch (e: Exception) {
                Log.d(TAG, "SendOffer-Req. Failed: $e")
                _sendOfferResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException, is SocketTimeoutException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

    private fun validateDetails(details: String) = if (details.isEmpty()) {
        detailsErrorLiveData.value =
            application.getString(R.string.empty_field_error_message)
        false
    } else {
        true
    }

    private fun validatePrice(price: String) = if (price.isEmpty()) {
        priceErrorLiveData.value =
            application.getString(R.string.empty_field_error_message)
        false
    } else {
        true
    }
}