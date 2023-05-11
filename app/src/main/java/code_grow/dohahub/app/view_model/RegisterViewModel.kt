package code_grow.dohahub.app.view_model

import android.app.Application
import android.util.Log
import android.util.Patterns
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

private const val TAG = "RegisterViewModel"

class RegisterViewModel(
    private val application: Application,
    private val repo: AuthRepository
) : ViewModel() {
    /* user username input */
    val userNameLiveData = MutableLiveData("")

    /* username error message */
    val userNameErrorLiveData = MutableLiveData("")

    /* user phone number input */
    val phoneNumberLiveData = MutableLiveData("")

    /* phone error message */
    val phoneNumberErrorLiveData = MutableLiveData("")

    /* user email input */
    val emailLiveData = MutableLiveData("")

    /* email error message */
    val emailErrorLiveData = MutableLiveData("")

    /* user password input */
    val passwordLiveData = MutableLiveData("")

    /* password error message */
    val passwordErrorLiveData = MutableLiveData("")

    /* user type input */
    val userTypeLiveData = MutableLiveData(false) /* true-> Provider, false-> user */

    private val _registerBooleanLiveData = MutableLiveData(false)
    val registerBooleanLiveData: LiveData<Boolean> get() = _registerBooleanLiveData

    fun startRegisterRequest(value: Boolean) {
        _registerBooleanLiveData.value = value
    }

    fun changeUserType(isProvider: Boolean) {
        userTypeLiveData.value = isProvider
    }

    /* validate register form */
    fun registerBtnClicked() {
        var pass = true
        if (!validateUsername(userNameLiveData.value!!))
            pass = false
        if (!validatePhone(phoneNumberLiveData.value!!))
            pass = false
        if (!validateEmail(emailLiveData.value!!))
            pass = false
        if (!validatePassword(passwordLiveData.value!!))
            pass = false

        if (pass)
            startRegisterRequest(true)
    }

    private val _registerResponse = MutableLiveData<Resource>(Resource.Idle)
    val registerResponse: LiveData<Resource> get() = _registerResponse

    /* call register req. */
    fun register() {
        viewModelScope.launch {
            val map = mutableMapOf<String, String>()
            map["password"] = passwordLiveData.value!!
            map["mobile"] = phoneNumberLiveData.value!!
            map["name"] = userNameLiveData.value!!
            map["username"] = emailLiveData.value!!/* email */
            map["role"] =
                if (userTypeLiveData.value!!) "2" else "1" /* isProvider send 2, isUser send 1 */
            /*
            password:123456
            mobile:0114413181
            name:amrsesee
            image:image.jpg
            username:amr.geek@gmail.com
            city_id:1234
            role:2
             */
            try {
                _registerResponse.value = Resource.Loading
                val apiResponse =
                    repo.register(map)
                if (apiResponse.status) {
                    _registerResponse.value = Resource.Success(apiResponse.data as User)
                } else {
                    _registerResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Register-Req. Failed: $e")
                _registerResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException, is SocketTimeoutException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

    private fun validatePhone(phone: String) = if (phone.isEmpty()) {
        phoneNumberErrorLiveData.value =
            application.getString(R.string.empty_field_error_message)
        false
    } else {
//        if (phone.length < 8) {
//            phoneNumberErrorLiveData.value =
//                application.getString(R.string.phone_length_error_message)
//            false
//        }else if (phone.length > 12) {
//            phoneNumberErrorLiveData.value =
//                application.getString(R.string.phone_length_error_message_1)
//            false
//        } else
            true
    }

    private fun validatePassword(password: String) = if (password.isEmpty()) {
        passwordErrorLiveData.value = application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (password.length < 6) {
            passwordErrorLiveData.value =
                application.getString(R.string.password_length_error_message)
            false
        } else
            true
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

    private fun validateUsername(username: String) = if (username.isEmpty()) {
        userNameErrorLiveData.value = application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (username.length < 3) {
            userNameErrorLiveData.value =
                application.getString(R.string.username_length_error_message)
            false
        } else if (username.length > 30) {
            userNameErrorLiveData.value =
                application.getString(R.string.username_length_error_message_1)
            false
        } else
            true
    }
}