package code_grow.dohahub.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.dohahub.app.R
import code_grow.dohahub.app.model.DynamicHomeData
import code_grow.dohahub.app.repository.HomeRepository
import code_grow.dohahub.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException
import java.security.Provider

private const val TAG = "ProvidersViewModel"

class ProvidersViewModel(
    private val categoryId: Int,
    private val query: String,
    val application: Application,
    val repo: HomeRepository
) : ViewModel() {
    private val _startRequestProviders = MutableLiveData(false)
    val startRequestProviders: LiveData<Boolean> get() = _startRequestProviders

    fun startRequestProviders(value: Boolean) {
        _startRequestProviders.value = value
    }

    private val _providersResponse = MutableLiveData<Resource>(Resource.Idle)
    val providersResponse: LiveData<Resource> get() = _providersResponse

    init {
        startRequestProviders(true)
    }

    fun getProviders() {
        val map = mutableMapOf<String, String>()
        // -1 -> no category selected
        // 0 -> explore all from featuredList
        // else -> certain category selected
        if (categoryId != -1) {
            if (categoryId == 0)
                map["featured"] = true.toString()
            else
                map["category_id"] = categoryId.toString()
        }

        if (query.isNotEmpty())
            map["search"] = query

        viewModelScope.launch {
            try {
                _providersResponse.value = Resource.Loading
                val apiResponse = repo.fetchProviders(map)
                if (apiResponse.status) {
                    val data = apiResponse.data as DynamicHomeData
                    if (data.providers.isEmpty())
                        _providersResponse.value = Resource.Empty
                    else
                        _providersResponse.value =
                            Resource.Success(data)
                } else {
                    _providersResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "providers-Req. Failed: $e")
                _providersResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }


    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException, is SocketTimeoutException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

}