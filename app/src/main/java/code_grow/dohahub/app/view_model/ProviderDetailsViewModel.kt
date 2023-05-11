package code_grow.dohahub.app.view_model

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.dohahub.app.R
import code_grow.dohahub.app.model.Product
import code_grow.dohahub.app.model.ProviderDetails
import code_grow.dohahub.app.repository.HomeRepository
import code_grow.dohahub.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException

private const val TAG = "ProviderDetailsViewModel"

class ProviderDetailsViewModel(
    val providerId: Int,
    val application: Application,
    val repo: HomeRepository
) : ViewModel() {
    private val _startRequestProviderDetails = MutableLiveData(false)
    val startRequestProviderDetails: LiveData<Boolean> get() = _startRequestProviderDetails

    private val _providerDetailsResponse = MutableLiveData<Resource>(Resource.Idle)
    val providerDetailsResponse: LiveData<Resource> get() = _providerDetailsResponse

    fun startRequestProviderDetails(value: Boolean) {
        _startRequestProviderDetails.value = value
    }

    init {
        startRequestProviderDetails(true)
    }

    @SuppressLint("LongLogTag")
    fun getProviderDetails() {
        viewModelScope.launch {
            try {
                _providerDetailsResponse.value = Resource.Loading
                val apiResponse = repo.fetchProviderDetails(providerId)
                if (apiResponse.status) {
                    _providerDetailsResponse.value =
                        Resource.Success(apiResponse.data as ProviderDetails)
                } else {
                    _providerDetailsResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "ProviderDetails-Req. Failed: $e")
                _providerDetailsResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException, is SocketTimeoutException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }
}