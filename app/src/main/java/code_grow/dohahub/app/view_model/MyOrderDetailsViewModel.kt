package code_grow.dohahub.app.view_model

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.view.View
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

private const val TAG = "MyOrderDetailsViewModel"

class MyOrderDetailsViewModel(
    private val providerId: Int,
    private val orderId: Int,
    private val application: Application,
    private val repo: OrdersRepository
) : ViewModel() {

    private val _startFinishingOrder = MutableLiveData(false)
    val startFinishingOrder: LiveData<Boolean> get() = _startFinishingOrder

    fun startFinishingOrder(value: Boolean) {
        _startFinishingOrder.value = value
    }

    private val _startDeletingOrder = MutableLiveData(false)
    val startDeletingOrder: LiveData<Boolean> get() = _startDeletingOrder

    fun startDeletingOrder(value: Boolean) {
        _startDeletingOrder.value = value
    }

    private val _deleteOrderResponse = MutableLiveData<Resource>(Resource.Idle)
    val deleteOrderResponse: LiveData<Resource> get() = _deleteOrderResponse

    private val _finishOrderResponse = MutableLiveData<Resource>(Resource.Idle)
    val finishOrderResponse: LiveData<Resource> get() = _finishOrderResponse

    val progressState = MutableLiveData<Resource>(Resource.Idle)

    private val _startSendingFeedback = MutableLiveData(false)
    val startSendingFeedback: LiveData<Boolean> get() = _startSendingFeedback

    fun startSendingFeedback(value: Boolean) {
        _startSendingFeedback.value = value
    }

    /* user feedback input */
    val feedbackLiveData = MutableLiveData("")

    /* feedback error message */
    val feedbackErrorLiveData = MutableLiveData("")

    private val _sendFeedbackResponse = MutableLiveData<Resource>(Resource.Idle)
    val sendFeedbackResponse: LiveData<Resource> get() = _sendFeedbackResponse


    private val _showConfirmDialog = MutableLiveData(false)
    val showConfirmDialog: LiveData<Boolean> get() = _showConfirmDialog

    fun showConfirmDialog(value: Boolean) {
        _showConfirmDialog.value = value
    }

    fun submitBtnClicked(status: Int) {
        /*
            0 => waiting for approve . also customer can delete the order in this case .
            1 => Accepted . he can start sending message . he can make finish to the order .
            2= > order finished , he can make a rate .
        */
        when (status) {
            0 -> showConfirmDialog(true)
            1 -> startFinishingOrder(true)
            2 -> {
                if (!validateFeedback(feedbackLiveData.value!!))
                    return
                startSendingFeedback(true)
            }
        }
    }

    fun deleteOrder() {
        viewModelScope.launch {
            val map = mutableMapOf<String, Int>()
            map["customer_id"] = UserInfo.userId
            map["user_id"] = providerId
            map["id"] = orderId


            try {
                _deleteOrderResponse.value = Resource.Loading
                progressState.value = Resource.Loading
                val apiResponse = repo.deleteOrder(map)
                if (apiResponse.status) {
                    _deleteOrderResponse.value = Resource.Success(apiResponse.message)
                } else {
                    _deleteOrderResponse.value = Resource.Failed(apiResponse.message)
                }
                progressState.value = Resource.Idle
            } catch (e: Exception) {
                Log.d(TAG, "DeleteOrder-Req. Failed: $e")
                progressState.value = Resource.Idle
                _deleteOrderResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    fun finishOrder() {
        viewModelScope.launch {
            val map = mutableMapOf<String, Int>()
            map["customer_id"] = UserInfo.userId
            map["user_id"] = providerId
            map["id"] = orderId


            try {
                _finishOrderResponse.value = Resource.Loading
                progressState.value = Resource.Loading
                val apiResponse = repo.finishOrder(map)
                if (apiResponse.status) {
                    _finishOrderResponse.value = Resource.Success(apiResponse.message)
                } else {
                    _finishOrderResponse.value = Resource.Failed(apiResponse.message)
                }
                progressState.value = Resource.Idle
            } catch (e: Exception) {
                Log.d(TAG, "FinishOrder-Req. Failed: $e")
                progressState.value = Resource.Idle
                _finishOrderResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    @SuppressLint("LongLogTag")
    fun sendFeedback() {
        viewModelScope.launch {
            val map = mutableMapOf<String, String>()
            map["customer_id"] = UserInfo.userId.toString()
            map["user_id"] = providerId.toString()
            map["id"] = orderId.toString()
            map["message"] = feedbackLiveData.value!!

            try {
                _sendFeedbackResponse.value = Resource.Loading
                progressState.value = Resource.Loading
                val apiResponse = repo.sendCustomerFeedback(map)
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