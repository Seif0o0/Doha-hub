package code_grow.dohahub.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.dohahub.app.R
import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.model.DynamicHomeData
import code_grow.dohahub.app.model.DynamicMyServiceData
import code_grow.dohahub.app.repository.MyServicesRepository
import code_grow.dohahub.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException

private const val TAG = "MyServicesViewModel"

class MyServicesViewModel(
    private val application: Application,
    private val repo: MyServicesRepository
) : ViewModel() {

    private val _startRequestMyServices = MutableLiveData(false)
    val startRequestMyServices: LiveData<Boolean> get() = _startRequestMyServices

    fun startRequestMyServices(value: Boolean) {
        _startRequestMyServices.value = value
    }

    private val _servicesResponse = MutableLiveData<Resource>(Resource.Idle)
    val servicesResponse: LiveData<Resource> get() = _servicesResponse

    init {
        startRequestMyServices(true)
    }

    fun getMyServices() {
        viewModelScope.launch {
            try {
                _servicesResponse.value = Resource.Loading
                val apiResponse = repo.fetchMyServices()
                if (apiResponse.status) {
                    val data = apiResponse.data as DynamicMyServiceData
                    if (data.myServices.isEmpty())
                        _servicesResponse.value = Resource.Empty
                    else
                        _servicesResponse.value =
                            Resource.Success(data)
                } else {
                    _servicesResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "services-Req. Failed: $e")
                _servicesResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private val _startRemoveService = MutableLiveData(false)
    val startRemoveService: LiveData<Boolean> get() = _startRemoveService

    fun startRemoveService(value: Boolean, serviceId: Int, position: Int) {
        if (serviceId != 0)
            this.serviceId = serviceId
        if (position != -1)
            this.position = position
        _startRemoveService.value = value
    }

    private val _removeServiceResponse = MutableLiveData<Resource>(Resource.Idle)
    val removeServiceResponse: LiveData<Resource> get() = _removeServiceResponse

    var serviceId: Int = 0
    var position: Int = -1
    fun removeService() {
        viewModelScope.launch {
            try {
                _removeServiceResponse.value = Resource.Loading
                val apiResponse = repo.removeService(serviceId)
                if (apiResponse.status) {
                    _removeServiceResponse.value = Resource.Success(apiResponse.message)
                } else {
                    _removeServiceResponse.value = Resource.Failed(apiResponse.message)
                }
            } catch (e: Exception) {
                Log.d(TAG, "RemoveService-Req. Failed: $e")
                _removeServiceResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException, is SocketTimeoutException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }
}