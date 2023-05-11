package code_grow.dohahub.app.view_model

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.dohahub.app.R
import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.repository.OrdersRepository
import code_grow.dohahub.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException

private const val TAG = "ClientOrderDetailsViewModel"

class ClientOrderDetailsViewModel(
    private val customerId: Int,
    private val orderId: Int,
    private val application: Application,
    private val repo: OrdersRepository
) : ViewModel() {

    val progressState = MutableLiveData<Resource>(Resource.Idle)

    private val _startAcceptingOrder = MutableLiveData(false)
    val startAcceptingOrder: LiveData<Boolean> get() = _startAcceptingOrder

    fun startAcceptingOrder(value: Boolean) {
        _startAcceptingOrder.value = value
    }

    private val _startSendingFeedback = MutableLiveData(false)
    val startSendingFeedback: LiveData<Boolean> get() = _startSendingFeedback

    fun startSendingFeedback(value: Boolean) {
        _startSendingFeedback.value = value
    }

    /* user feedback input */
    val feedbackLiveData = MutableLiveData("")

    /* feedback error message */
    val feedbackErrorLiveData = MutableLiveData("")

    private val _acceptOrderResponse = MutableLiveData<Resource>(Resource.Idle)
    val acceptOrderResponse: LiveData<Resource> get() = _acceptOrderResponse

    private val _sendFeedbackResponse = MutableLiveData<Resource>(Resource.Idle)
    val sendFeedbackResponse: LiveData<Resource> get() = _sendFeedbackResponse

    fun submitBtnClicked(status: Int) {
        /*
            0 => waiting for your approve . ( he will make confirm )
            1 => Confirmed , he can start sending message
            2 => Finished
        */
        when (status) {
            0 -> startAcceptingOrder(true)
            2 -> {
                if (!validateFeedback(feedbackLiveData.value!!))
                    return
                startSendingFeedback(true)
            }
        }
    }

    @SuppressLint("LongLogTag")
    fun acceptOrder() {
        viewModelScope.launch {
            val map = mutableMapOf<String, Int>()
            map["customer_id"] = customerId
            map["user_id"] = UserInfo.userId
            map["id"] = orderId

            try {
                _acceptOrderResponse.value = Resource.Loading
                progressState.value = Resource.Loading
                val apiResponse = repo.acceptOrder(map)
                if (apiResponse.status) {
                    _acceptOrderResponse.value = Resource.Success(apiResponse.message)
                } else {
                    _acceptOrderResponse.value = Resource.Failed(apiResponse.message)
                }
                progressState.value = Resource.Idle
            } catch (e: Exception) {
                Log.d(TAG, "AcceptOrder-Req. Failed: $e")
                progressState.value = Resource.Idle
                _acceptOrderResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    @SuppressLint("LongLogTag")
    fun sendFeedback() {
        viewModelScope.launch {
            val map = mutableMapOf<String, String>()
            map["user_id"] = UserInfo.userId.toString()
            map["id"] = orderId.toString()
            map["message"] = feedbackLiveData.value!!

            try {
                _sendFeedbackResponse.value = Resource.Loading
                progressState.value = Resource.Loading
                val apiResponse = repo.sendProviderFeedback(map)
                if (apiResponse.status) {
                    _sendFeedbackResponse.value = Resource.Success(apiResponse.message)
                } else {
                    _sendFeedbackResponse.value = Resource.Failed(apiResponse.message)
                }
                progressState.value = Resource.Idle
            } catch (e: Exception) {
                Log.d(TAG, "SendFeedback-Req. Failed: $e")
                progressState.value = Resource.Idle
                _sendFeedbackResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun validateFeedback(feedback: String) = if (feedback.isEmpty()) {
        feedbackErrorLiveData.value =
            application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (feedback.length < 15) {
            feedbackErrorLiveData.value =
                application.getString(R.string.feedback_length_error_message)
            false
        } else
            true
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException, is SocketTimeoutException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }
}