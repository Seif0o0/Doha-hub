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
import code_grow.dohahub.app.model.Campaign
import code_grow.dohahub.app.model.CampaignCategory
import code_grow.dohahub.app.repository.CampaignsRepository
import code_grow.dohahub.app.retrofit.Resource
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.SocketTimeoutException

private const val TAG = "EditCampaignViewModel"

class EditCampaignViewModel(
    private val campaign: Campaign,
    private val application: Application,
    private val repo: CampaignsRepository
) : ViewModel() {
    /* title input */
    val titleLiveData = MutableLiveData(campaign.title)

    /* title error message */
    val titleErrorLiveData = MutableLiveData("")

    /* details input */
    val detailsLiveData = MutableLiveData(campaign.details)

    /* details error message */
    val detailsErrorLiveData = MutableLiveData("")

    /* price input */
    val priceLiveData = MutableLiveData(campaign.suggestedPrice)

    /* price error message */
    val priceErrorLiveData = MutableLiveData("")

    /* category input */
    val categoryLiveData = MutableLiveData("")

    /* category error message */
    val categoryErrorLiveData = MutableLiveData("")

    val loadingProgress = MutableLiveData<Resource>(Resource.Idle)

    private val _startEditingCampaign = MutableLiveData(false)
    val startEditingCampaign: LiveData<Boolean> get() = _startEditingCampaign

    fun startEditingCampaign(value: Boolean) {
        _startEditingCampaign.value = value
    }

    private val _categoriesResponse = MutableLiveData<Resource>(Resource.Idle)
    val categoriesResponse: LiveData<Resource> get() = _categoriesResponse

    val startRequestCategories = MutableLiveData(false)

    fun startRequestCategories(value: Boolean) {
        startRequestCategories.value = value
    }

    init {
        startRequestCategories(true)
    }

    fun getCategories() {
        viewModelScope.launch {
            try {
                loadingProgress.value = Resource.Loading
                _categoriesResponse.value = Resource.Loading
                val apiResponse = repo.fetchCategories()
                if (apiResponse.status) {
                    _categoriesResponse.value = Resource.Success(apiResponse.data)
                } else {
                    _categoriesResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: java.lang.Exception) {
                Log.d(TAG, "Categories-Req. Failed: $e")
                _categoriesResponse.value = Resource.Failed(catchNetworkException(e))
            } finally {
                loadingProgress.value = Resource.Idle
            }
        }
    }

    fun submitBtnClicked() {
        var pass = true
        if (_uploadImageResponse.value is Resource.Loading) {
            imageMessageLiveData.value =
                application.getString(R.string.wait_uploading_photo_message)
            pass = false
        }

        if (!validateTitle(titleLiveData.value!!))
            pass = false
        if (!validateDetails(detailsLiveData.value!!))
            pass = false
        if (!validatePrice(priceLiveData.value!!))
            pass = false
        if (!validateCategory(categoryLiveData.value!!))
            pass = false

        if (pass)
            startEditingCampaign(true)
    }

    private val _editCampaignResponse = MutableLiveData<Resource>(Resource.Idle)
    val editCampaignResponse: LiveData<Resource> get() = _editCampaignResponse

    fun editCampaign() {
        viewModelScope.launch {
            val map = mutableMapOf<String, String>()

            map["id"] = campaign.campaignId.toString()
            map["user_id"] = UserInfo.userId.toString()
            map["title"] = titleLiveData.value!!
            map["body"] = detailsLiveData.value!!
            map["suggested_price"] = priceLiveData.value!!

            if (uploadedImage.isNotEmpty())
                map["image"] = uploadedImage

            val categories =
                (_categoriesResponse.value!! as Resource.Success<MutableList<CampaignCategory>>).data
            map["category_id"] = categories.find {
                it.name == categoryLiveData.value!!
            }?.categoryId.toString()

            try {
                loadingProgress.value = Resource.Loading
                _editCampaignResponse.value = Resource.Loading
                val apiResponse = repo.editCampaign(map)
                if (apiResponse.status) {
                    _editCampaignResponse.value = Resource.Success(apiResponse.message)
                } else {
                    _editCampaignResponse.value = Resource.Failed(apiResponse.message)
                }
            } catch (e: Exception) {
                Log.d(TAG, "EditCampaign-Req. Failed: $e")
                _editCampaignResponse.value = Resource.Failed(catchNetworkException(e))
            } finally {
                loadingProgress.value = Resource.Idle
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException, is SocketTimeoutException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

    private val _startDeletingCampaign = MutableLiveData(false)
    val startDeletingCampaign: LiveData<Boolean> get() = _startDeletingCampaign

    fun startDeletingCampaign(value: Boolean) {
        _startDeletingCampaign.value = value
    }

    private val _deleteCampaignResponse = MutableLiveData<Resource>(Resource.Idle)
    val deleteCampaignResponse: LiveData<Resource> get() = _deleteCampaignResponse

    fun deleteCampaign() {
        viewModelScope.launch {
            val map = mutableMapOf<String, String>()
            map["id"] = campaign.campaignId.toString()
            map["user_id"] = UserInfo.userId.toString()

            try {
                loadingProgress.value = Resource.Loading
                _deleteCampaignResponse.value = Resource.Loading
                val apiResponse = repo.deleteCampaign(map)
                if (apiResponse.status) {
                    _deleteCampaignResponse.value = Resource.Success(apiResponse.message)
                } else {
                    _deleteCampaignResponse.value = Resource.Failed(apiResponse.message)
                }
                loadingProgress.value = Resource.Idle
            } catch (e: Exception) {
                Log.d(TAG, "EditCampaign-Req. Failed: $e")
                loadingProgress.value = Resource.Idle
                _deleteCampaignResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private val _startUploadingImage = MutableLiveData(false)
    val startUploadingImage: LiveData<Boolean> get() = _startUploadingImage

    fun setStartUploadingImage(value: Boolean, image: File?) {
        if (image != null)
            this.file = image
        _startUploadingImage.value = value
    }

    var uploadedImage =
        if (campaign.photo.isEmpty() || campaign.photo.contains("user_img/")) "" else campaign.photo.split(
            "campaign_img/"
        )[1]
    private val _uploadImageResponse =
        MutableLiveData<Resource>(
            Resource.Success(
                if (campaign.photo.isEmpty() || campaign.photo.contains("user_img/")) "" else campaign.photo.split(
                    "campaign_img/"
                )[1]
            )
        )
    val uploadImageResponse: LiveData<Resource> get() = _uploadImageResponse
    val imageMessageLiveData = MutableLiveData("")
    var cancelUploadingImage =
        false /* if user tried to remove his image while it getting uploaded */
    private lateinit var file: File

    /*
        Start uploading report using MultiPart request
        (uploadedImage = "") in case if user uploaded image successfully before and decided to change it before editing the campaign. so if the second one failed we have to reset the last one
     */
    @SuppressLint("LongLogTag")
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

    private fun validateTitle(title: String) = if (title.isEmpty()) {
        titleErrorLiveData.value = application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (title.length < 3) {
            titleErrorLiveData.value =
                application.getString(R.string.campaign_title_length_error_message)
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

    private fun validatePrice(price: String) = if (price.isEmpty()) {
        priceErrorLiveData.value =
            application.getString(R.string.empty_field_error_message)
        false
    } else {
        true
    }

    private fun validateCategory(category: String) = if (category.isEmpty()) {
        categoryErrorLiveData.value =
            application.getString(R.string.empty_spinner_field_error_message)
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