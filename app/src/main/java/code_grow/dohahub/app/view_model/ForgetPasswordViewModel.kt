package code_grow.dohahub.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.dohahub.app.R
import code_grow.dohahub.app.model.User
import code_grow.dohahub.app.repository.AuthRepository
import code_grow.dohahub.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException
import java.util.regex.Pattern

private const val TAG = "ForgetPasswordViewModel"

class ForgetPasswordViewModel(
    private val application: Application,
    private val repo: AuthRepository
) : ViewModel() {
    /* user email input */
    val emailLiveData = MutableLiveData("")

    /* email error message */
    val emailErrorLiveData = MutableLiveData("")

    private val _startSendCode = MutableLiveData(false)
    val startSendCode: LiveData<Boolean> get() = _startSendCode

    fun startSendCode(value: Boolean) {
        _startSendCode.value = value
    }

    private val _sendCodeResponse = MutableLiveData<Resource>(Resource.Idle)
    val sendCodeResponse: LiveData<Resource> get() = _sendCodeResponse
    fun changeResponseToIdle(){
        _sendCodeResponse.value = Resource.Idle
    }
    fun sendCodeBtnClicked() {
        if (!validateEmail(emailLiveData.value!!))
            return

        startSendCode(true)
    }

    fun sendCode() {
        viewModelScope.launch {
            try {
                _sendCodeResponse.value = Resource.Loading
                val apiResponse = repo.forgetPassword(emailLiveData.value!!)
                if (apiResponse.status) {
                    _sendCodeResponse.value = Resource.Success(true)
                } else {
                    _sendCodeResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "SendCode-Req. Failed: $e")
                _sendCodeResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }


    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException, is SocketTimeoutException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

    private fun validateEmail(email: String) = if (email.isEmpty()) {
        emailErrorLiveData.value =
            application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (validateEmailFormat(email))
            true
        else {
            emailErrorLiveData.value =
                application.getString(R.string.email_validation_error_message)
            false
        }
    }

    private fun validateEmailFormat(email: String): Boolean {
        val regExpression =
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        val pattern = Pattern.compile(regExpression, Pattern.CASE_INSENSITIVE)

        return pattern.matcher(email).matches()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG,"cleared")
    }
}