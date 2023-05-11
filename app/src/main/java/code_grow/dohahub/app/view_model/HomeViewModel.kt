package code_grow.dohahub.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.dohahub.app.R
import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.model.User
import code_grow.dohahub.app.repository.AuthRepository
import code_grow.dohahub.app.repository.HomeRepository
import code_grow.dohahub.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException

private const val TAG = "HomeViewModel"

class HomeViewModel(
    val application: Application,
    val repo: HomeRepository,
    val authRepository: AuthRepository
) : ViewModel() {
    private val _startRequestHomeData = MutableLiveData(false)
    val startRequestHomeData: LiveData<Boolean> get() = _startRequestHomeData

    private val _homeResponse = MutableLiveData<Resource>(Resource.Idle)
    val homeResponse: LiveData<Resource> get() = _homeResponse

    fun startRequestHomeData(value: Boolean) {
        _startRequestHomeData.value = value
    }

    private val _startUpdateToken = MutableLiveData(false)
    val startUpdateToken: LiveData<Boolean> get() = _startUpdateToken

    fun startUpdateToken(value: Boolean) {
        _startUpdateToken.value = value
    }

    private val _updateTokenResponse = MutableLiveData<Resource>(Resource.Idle)
    val updateTokenResponse: LiveData<Resource> get() = _updateTokenResponse


    private val _categoriesResponse = MutableLiveData<Resource>(Resource.Idle)
    val categoriesResponse: LiveData<Resource> get() = _categoriesResponse

    init {
        startRequestHomeData(true)
        getCategories()
    }

    fun getHomeData() {
        viewModelScope.launch {
            try {
                _homeResponse.value = Resource.Loading
                val apiResponse = repo.fetchHomeData()
                if (apiResponse.status) {
                    _homeResponse.value = Resource.Success(apiResponse.homeData)
                } else {
                    _homeResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "HomeData-Req. Failed: $e")
                _homeResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }


    /*
        won't handle error in home fragment ... it will be handled in FilterDialogFragment
     */
    private fun getCategories() {
        viewModelScope.launch {
            try {
                _categoriesResponse.value = Resource.Loading
                val apiResponse = repo.fetchCategories()
                if (apiResponse.status) {
                    _categoriesResponse.value = Resource.Success(apiResponse.data)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Categories-Req. Failed: $e")
            }
        }
    }


    fun updateToken() {
        viewModelScope.launch {
            try {
                _updateTokenResponse.value = Resource.Loading
                val apiResponse = authRepository.updateToken(
                    mutableMapOf(
                        "id" to UserInfo.userId.toString(),
                        "token" to UserInfo.firebaseToken
                    )
                )
                if (apiResponse.status) {
                    _updateTokenResponse.value = Resource.Success(apiResponse.data as User)
                } else {
                    _updateTokenResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "UpdateToken-Req. Failed: $e")
//                _updateTokenResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }


    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException, is SocketTimeoutException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

}