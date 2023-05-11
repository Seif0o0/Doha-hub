package code_grow.dohahub.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.dohahub.app.R
import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.repository.HomeRepository
import code_grow.dohahub.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException

private const val TAG = "CheckoutViewModel"

class CheckoutViewModel(
    val application: Application,
    val repo: HomeRepository
) : ViewModel() {
    /* date input */
    val dateLiveData = MutableLiveData("")

    /* date error message */
    val dateErrorLiveData = MutableLiveData("")

    private val _startCheckoutBooleanLiveData = MutableLiveData(false)
    val startCheckoutBooleanLiveData: LiveData<Boolean> get() = _startCheckoutBooleanLiveData

    fun startCheckout(value: Boolean) {
        _startCheckoutBooleanLiveData.value = value
    }

    private val _checkoutResponse = MutableLiveData<Resource>(Resource.Idle)
    val checkoutResponse: LiveData<Resource> get() = _checkoutResponse

    private val map = mutableMapOf<String, String>()

    fun continueBtnClicked(
        userId: String,
        serviceId: String,
        categoryId: String,
        price: String
    ) {
        var pass = true
        if (!validateDate(dateLiveData.value!!))
            pass = false

        if (pass) {
            map.clear()
            map["customer_id"] = UserInfo.userId.toString()
            map["user_id"] = userId
            map["service_id"] = serviceId
            map["category_id"] = categoryId
            map["selected_date"] = dateLiveData.value!!.replace("/", "-")
            map["price"] = price
            startCheckout(true)
        }
    }

    fun checkout() {
        viewModelScope.launch {
            try {
                _checkoutResponse.value = Resource.Loading
                val apiResponse = repo.makeServiceCheckout(map)
                if (apiResponse.status) {
                    _checkoutResponse.value = Resource.Success(apiResponse.message)
                } else {
                    _checkoutResponse.value = Resource.Failed(apiResponse.message)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Checkout-Req. Failed: $e")
                _checkoutResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException, is SocketTimeoutException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

    private fun validateDate(details: String) = if (details.isEmpty()) {
        dateErrorLiveData.value =
            application.getString(R.string.empty_field_error_message)
        false
    } else {
        true
    }
}