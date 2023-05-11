package code_grow.dohahub.app.view_model

import android.app.Application
import android.util.Log
import android.util.Patterns
import android.view.View
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.regex.Pattern

private const val TAG = "ProfileViewModel"

class ProfileViewModel(
    private val application: Application,
    private val repo: AuthRepository
) : ViewModel() {

    /* user username input */
    val userNameLiveData = MutableLiveData(UserInfo.username)

    /* username error message */
    val userNameErrorLiveData = MutableLiveData("")

    /* user phone number input */
    val phoneNumberLiveData = MutableLiveData(UserInfo.phoneNumber)

    /* phone error message */
    val phoneNumberErrorLiveData = MutableLiveData("")

    /* user email input */
    val emailLiveData = MutableLiveData(UserInfo.email)

    /* email error message */
    val emailErrorLiveData = MutableLiveData("")

    /* user brief input */
    val briefLiveData = MutableLiveData(UserInfo.brief)

    /* brief error message */
    val briefErrorLiveData = MutableLiveData("")

    private val _editProfileBooleanLiveData = MutableLiveData(false)
    val editProfileBooleanLiveData: LiveData<Boolean> get() = _editProfileBooleanLiveData

    fun startEditProfileRequest(value: Boolean) {
        _editProfileBooleanLiveData.value = value
    }


    /* validate editProfile form */
    fun editProfileBtnClicked() {
        var pass = true
        if (_uploadImageResponse.value is Resource.Loading) {
            imageMessageLiveData.value =
                application.getString(R.string.wait_uploading_photo_message)
            pass = false
        }
        if (!validateUsername(userNameLiveData.value!!))
            pass = false
        if (!validatePhone(phoneNumberLiveData.value!!))
            pass = false
        if (!validateEmail(emailLiveData.value!!))
            pass = false
        if (!validateBrief(briefLiveData.value!!))
            pass = false

//        if (UserInfo.username == userNameLiveData.value!! &&
//            UserInfo.phoneNumber == phoneNumberLiveData.value!! &&
//            UserInfo.email == emailLiveData.value!! &&
//            uploadedImage.isEmpty()
//        ) {
//            pass = false
//        }


        /* start edit req */
        if (pass)
            startEditProfileRequest(true)

    }

    private val _editProfileResponse = MutableLiveData<Resource>(Resource.Idle)
    val editProfileResponse: LiveData<Resource> get() = _editProfileResponse

    /* call edit req. */
    fun editProfile() {
        viewModelScope.launch {
            val map = mutableMapOf<String, String>()
            map["id"] = UserInfo.userId.toString()
            map["mobile"] = phoneNumberLiveData.value!!
            map["name"] = userNameLiveData.value!!
            map["username"] = emailLiveData.value!!/* email */
            map["breif"] = briefLiveData.value!!
            if (uploadedImage.isNotEmpty())
                map["image"] = uploadedImage

            try {
                _editProfileResponse.value = Resource.Loading
                val apiResponse = repo.editProfile(map)
                if (apiResponse.status) {
                    _editProfileResponse.value = Resource.Success(apiResponse.data as User)
                } else {
                    _editProfileResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "EditProfileViewModel-Req. Failed: $e")
                _editProfileResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException, is SocketTimeoutException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

    private val _startUploadingImage = MutableLiveData(false)
    val startUploadingImage: LiveData<Boolean> get() = _startUploadingImage

    fun setStartUploadingImage(value: Boolean, image: File?) {
        if (image != null)
            this.file = image
        _startUploadingImage.value = value
    }

    private val _uploadImageResponse = MutableLiveData<Resource>(Resource.Idle)
    val uploadImageResponse: LiveData<Resource> get() = _uploadImageResponse
    val imageMessageLiveData = MutableLiveData("")
    private var uploadedImage = ""
    private lateinit var file: File

    /*
        Start uploading image using MultiPart request
        (uploadedImage = "") in case if user uploaded image successfully before and decided to change it before EditProfile. so if the second one failed we have to reset the last one
     */
    fun uploadImage() {
        viewModelScope.launch {
            try {
                val fileRequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData(
                    "parameters[0]",
                    file.name,
                    fileRequestBody
                )

                _uploadImageResponse.value = Resource.Loading
                val apiResponse = repo.uploadUserImage(body)
                if (!apiResponse.data.isNullOrEmpty()) {
                    uploadedImage =
                        apiResponse.data[0] /* response is list & the uploaded data is one image so we get the first object of the list */
                    _uploadImageResponse.value = Resource.Success(apiResponse.data)
                } else {
                    uploadedImage = ""
                    if (apiResponse.message != null) {
                        _uploadImageResponse.value = Resource.Failed(apiResponse.message)
                    } else {
                        _uploadImageResponse.value =
                            Resource.Failed(application.getString(R.string.something_went_wrong_try_again_later))
                    }
                }
            } catch (fileException: FileNotFoundException) {
                uploadedImage = ""
                Log.d(TAG, "UploadPhoto-FileNotFound: ${fileException.message}")
                _uploadImageResponse.value = Resource.Failed(fileException.message.toString())
            } catch (e: Exception) {
                uploadedImage = ""
                Log.d(TAG, "UploadPhoto-reqFailed: $e")
                _uploadImageResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
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

    private fun validateBrief(brief: String) = if (brief.isEmpty()) {
        briefErrorLiveData.value = application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (brief.length < 10) {
            briefErrorLiveData.value =
                application.getString(R.string.brief_length_error_message)
            false
        } else
            true
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
//        } else if (phone.length > 12) {
//            phoneNumberErrorLiveData.value =
//                application.getString(R.string.phone_length_error_message_1)
//            false
//        } else
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
}