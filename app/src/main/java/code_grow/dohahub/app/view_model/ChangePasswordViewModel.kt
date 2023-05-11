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
import code_grow.dohahub.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException

private const val TAG = "ChangePasswordViewModel"

class ChangePasswordViewModel(
    private val application: Application,
    private val repo: AuthRepository
) : ViewModel() {
    /* user new password input */
    val newPasswordLiveData = MutableLiveData("")

    /* new password error message */
    val newPasswordErrorLiveData = MutableLiveData("")

    /* user confirm new password input */
    val confirmNewPasswordLiveData = MutableLiveData("")

    /* new password error message */
    val confirmNewPasswordErrorLiveData = MutableLiveData("")

    private val _changePasswordBooleanLiveData = MutableLiveData(false)
    val changePasswordBooleanLiveData: LiveData<Boolean> get() = _changePasswordBooleanLiveData

    fun startChangePasswordRequest(value: Boolean) {
        _changePasswordBooleanLiveData.value = value
    }

    fun changePasswordClicked() {
        var pass = true
        if (!validateNewPassword(newPasswordLiveData.value!!))
            pass = false
        if (!validateConfirmNewPassword(confirmNewPasswordLiveData.value!!))
            pass = false

        if (pass)
            startChangePasswordRequest(true)
    }

    private val _changePasswordResponse = MutableLiveData<Resource>(Resource.Idle)
    val changePasswordResponse: LiveData<Resource> get() = _changePasswordResponse
    fun changePassword() {
        viewModelScope.launch {
            try {
                _changePasswordResponse.value = Resource.Loading
                val apiResponse = repo.changePassword(
                    mutableMapOf(
                        "id" to UserInfo.userId.toString(),
                        "new_password" to newPasswordLiveData.value!!
                    )
                )
                if (apiResponse.status) {
                    _changePasswordResponse.value = Resource.Success(true)
                } else {
                    _changePasswordResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "ChangePassword-Req. Failed: $e")
                _changePasswordResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException, is SocketTimeoutException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

    private fun validateNewPassword(password: String) = if (password.isEmpty()) {
        newPasswordErrorLiveData.value = application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (password.length < 6) {
            newPasswordErrorLiveData.value =
                application.getString(R.string.password_length_error_message)
            false
        } else
            true
    }

    private fun validateConfirmNewPassword(password: String) = if (password.isEmpty()) {
        confirmNewPasswordErrorLiveData.value =
            application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (password != newPasswordLiveData.value!!) {
            confirmNewPasswordErrorLiveData.value =
                application.getString(R.string.confirmation_password_error_message)
            false
        } else
            true
    }

}