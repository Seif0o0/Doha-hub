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
import code_grow.dohahub.app.repository.ArticleRepository
import code_grow.dohahub.app.retrofit.Resource
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.SocketTimeoutException

private const val TAG = "AddArticleViewModel"

class AddArticleViewModel(
    private val application: Application,
    private val repo: ArticleRepository
) : ViewModel() {

    /* name input */
    val nameLiveData = MutableLiveData("")

    /* name error message */
    val nameErrorLiveData = MutableLiveData("")

    /* details input */
    val detailsLiveData = MutableLiveData("")

    /* details error message */
    val detailsErrorLiveData = MutableLiveData("")

    private val _startAddingArticle = MutableLiveData(false)
    val startAddingArticle: LiveData<Boolean> get() = _startAddingArticle

    fun startAddingArticle(value: Boolean) {
        _startAddingArticle.value = value
    }


    fun submitBtnClicked() {
        var pass = true
        if (_uploadImageResponse.value is Resource.Loading) {
            imageMessageLiveData.value =
                application.getString(R.string.wait_uploading_photo_message)
            pass = false
        }
        if (!validateName(nameLiveData.value!!))
            pass = false
        if (!validateDetails(detailsLiveData.value!!))
            pass = false


        if (pass)
            startAddingArticle(true)
    }

    private val _addArticleResponse = MutableLiveData<Resource>(Resource.Idle)
    val addArticleResponse: LiveData<Resource> get() = _addArticleResponse

    fun addArticle() {
        viewModelScope.launch {
            //title=new product&body=new product body&user_id=1&image=image.jpg
            val map = mutableMapOf<String, String>()
            map["title"] = nameLiveData.value!!
            map["body"] = detailsLiveData.value!!
            map["user_id"] = UserInfo.userId.toString()
            if (uploadedImage.isNotEmpty())
                map["image"] = uploadedImage

            try {
                _addArticleResponse.value = Resource.Loading
                val apiResponse = repo.addNewArticle(map)
                if (apiResponse.status) {
                    _addArticleResponse.value = Resource.Success(apiResponse.message)
                } else {
                    _addArticleResponse.value = Resource.Failed(apiResponse.message)
                }
            } catch (e: Exception) {
                Log.d(TAG, "AddArticle-Req. Failed: $e")
                _addArticleResponse.value = Resource.Failed(catchNetworkException(e))
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
    var uploadedImage = ""
    var cancelUploadingImage =
        false /* if user tried to remove his image while it getting uploaded */
    private lateinit var file: File

    /*
        Start uploading report using MultiPart request
        (uploadedImage = "") in case if user uploaded image successfully before and decided to change it before adding new article. so if the second one failed we have to reset the last one
     */
    @SuppressLint("LongLogTag")
    fun uploadReport() {
        viewModelScope.launch {
            try {

                val fileRequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData(
                    "parameters[0]",
                    file.name,
                    fileRequestBody
                )
                _uploadImageResponse.value = Resource.Loading
                val apiResponse = repo.uploadImage(body)
                if (!cancelUploadingImage) {
                    if (!apiResponse.data.isNullOrEmpty()) {
                        uploadedImage =
                            apiResponse.data[0] /* response is list & the uploaded data is one reportImage so we get the first object of the list */
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
                } else
                    _uploadImageResponse.value = Resource.Idle
                cancelUploadingImage = false
            } catch (fileException: FileNotFoundException) {
                if (!cancelUploadingImage) {
                    uploadedImage = ""
                    Log.d(TAG, "UploadImage-FileNotFound: ${fileException.message}")
                    _uploadImageResponse.value = Resource.Failed(fileException.message.toString())
                } else
                    _uploadImageResponse.value = Resource.Idle
                cancelUploadingImage = false

            } catch (e: java.lang.Exception) {
                if (!cancelUploadingImage) {
                    uploadedImage = ""
                    Log.d(TAG, "UploadImage-reqFailed: $e")
                    _uploadImageResponse.value = Resource.Failed(catchNetworkException(e))
                } else
                    _uploadImageResponse.value = Resource.Idle
                cancelUploadingImage = false
            }
        }
    }

    private fun validateName(name: String) = if (name.isEmpty()) {
        nameErrorLiveData.value = application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (name.length < 3) {
            nameErrorLiveData.value =
                application.getString(R.string.article_name_length_error_message)
            false
        } else
            true
    }

    private fun validateDetails(details: String) = if (details.isEmpty()) {
        detailsErrorLiveData.value =
            application.getString(R.string.empty_field_error_message)
        false
    } else {
        true
    }

    fun removePhoto() {
        uploadedImage = ""
        if (_uploadImageResponse.value is Resource.Loading) {
            cancelUploadingImage = true
        }
    }
}