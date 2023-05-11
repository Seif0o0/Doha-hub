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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException

private const val TAG = "LoginViewModel"

class LoginViewModel(
    private val application: Application,
    private val repo: AuthRepository
) :
    ViewModel() {
    var firebaseToken = ""

    /* user phone number input */
    val phoneNumberLiveData = MutableLiveData("")

    /* phone error message */
    val phoneNumberErrorLiveData = MutableLiveData("")

    /* user password input */
    val passwordLiveData = MutableLiveData("")

    /* password error message */
    val passwordErrorLiveData = MutableLiveData("")


    val loadingProgress = MutableLiveData<Resource>(Resource.Idle)

    private val _loginBooleanLiveData = MutableLiveData(false)
    val loginBooleanLiveData: LiveData<Boolean> get() = _loginBooleanLiveData

    fun startLoginRequest(value: Boolean) {
        _loginBooleanLiveData.value = value
    }

    private val _startCheckSocial = MutableLiveData(false)
    val startCheckSocial: LiveData<Boolean> get() = _startCheckSocial
    var email = ""
    var username = ""
    fun startCheckSocial(value: Boolean, email: String? = null, username: String? = null) {
        if (value) this.email = email ?: ""
        if (value) this.username = username ?: ""
        Log.d(TAG, "Status: $value, email:${this.email},username:${this.username}")
        _startCheckSocial.value = value
    }

    private val _checkSocialResponse = MutableLiveData<Resource>(Resource.Idle)
    val checkSocialResponse: LiveData<Resource> get() = _checkSocialResponse

    fun checkSocial() {
        viewModelScope.launch {
            try {
                loadingProgress.value = Resource.Loading
                _checkSocialResponse.value = Resource.Loading
                val apiResponse =
                    repo.socialCheck(email)
                if (apiResponse.status) {
                    _checkSocialResponse.value = Resource.Success(apiResponse.data)
                } else {
                    if (apiResponse.message != null) {
                        _checkSocialResponse.value = Resource.Failed(apiResponse.message)
                    } else {
                        _checkSocialResponse.value = Resource.Failed("true")
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "checkSocial-Req. Failed: $e")
                _checkSocialResponse.value = Resource.Failed(catchNetworkException(e))
            } finally {
                loadingProgress.value = Resource.Idle
            }
        }
    }

    /* validate login form */
    fun loginBtnCLicked() {
        var pass = true
        if (!validatePhone(phoneNumberLiveData.value!!))
            pass = false
        if (!validatePassword(passwordLiveData.value!!))
            pass = false

        if (pass)
            startLoginRequest(true)
    }

    private val _loginResponse = MutableLiveData<Resource>(Resource.Idle)
    val loginResponse: LiveData<Resource> get() = _loginResponse

    /* call login req. */
    fun login() {
        viewModelScope.launch {
            try {
                loadingProgress.value = Resource.Loading
                _loginResponse.value = Resource.Loading
                val apiResponse =
                    repo.login(phoneNumberLiveData.value!!, passwordLiveData.value!!, firebaseToken)
                if (apiResponse.status) {
                    _loginResponse.value = Resource.Success(apiResponse.data as User)
                } else {
                    _loginResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Login-Req. Failed: $e")
                _loginResponse.value = Resource.Failed(catchNetworkException(e))
            } finally {
                loadingProgress.value = Resource.Idle
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
        true
    }

    private fun validatePassword(password: String) = if (password.isEmpty()) {
        passwordErrorLiveData.value = application.getString(R.string.empty_field_error_message)
        false
    } else {
        true
    }

    fun convertCheckSocialToIdle() {
        _checkSocialResponse.value = Resource.Idle
    }

    fun convertLoginToIdle() {
        _loginResponse.value = Resource.Idle
    }
}