package code_grow.dohahub.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.dohahub.app.R
import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.model.Category
import code_grow.dohahub.app.model.MyService
import code_grow.dohahub.app.model.Provider
import code_grow.dohahub.app.repository.MyServicesRepository
import code_grow.dohahub.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException

private const val TAG = "EditServiceViewModel"

class EditServiceViewModel(
    private val service: MyService?,
    private val app: Application,
    private val repo: MyServicesRepository
) : ViewModel() {
    /* title input */
    val titleLiveData = MutableLiveData(service!!.title)

    /* title error message */
    val titleErrorLiveData = MutableLiveData("")

    /* details input */
    val detailsLiveData = MutableLiveData(service!!.categoryBrief)

    /* details error message */
    val detailsErrorLiveData = MutableLiveData("")

    /* price input */
    val priceLiveData = MutableLiveData(service!!.categoryPrice.toString())

    /* price error message */
    val priceErrorLiveData = MutableLiveData("")

    /* category input */
    val categoryLiveData = MutableLiveData("")

    /* category error message */
    val categoryErrorLiveData = MutableLiveData("")

    val progressState = MutableLiveData<Resource>(Resource.Idle)

    private val _startEditingService = MutableLiveData(false)
    val startEditingService: LiveData<Boolean> get() = _startEditingService

    fun startEditingService(value: Boolean) {
        _startEditingService.value = value
    }

    private val _categoriesResponse = MutableLiveData<Resource>(Resource.Idle)
    val categoriesResponse: LiveData<Resource> get() = _categoriesResponse

    private val _startRequestCategories = MutableLiveData(false)
    val startRequestCategories: LiveData<Boolean> get() = _startRequestCategories

    fun startRequestCategories(value: Boolean) {
        _startRequestCategories.value = value
    }

    init {
        startRequestCategories(true)
    }

    fun getCategories() {
        viewModelScope.launch {
            try {
                _categoriesResponse.value = Resource.Loading
                progressState.value = Resource.Loading
                val apiResponse = repo.fetchCategories()
                if (apiResponse.status) {
                    _categoriesResponse.value = Resource.Success(apiResponse.data)
                } else {
                    _categoriesResponse.value = Resource.Failed(apiResponse.message!!)
                }
                progressState.value = Resource.Idle
            } catch (e: java.lang.Exception) {
                Log.d(TAG, "Categories-Req. Failed: $e")
                progressState.value = Resource.Idle
                _categoriesResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    fun submitBtnClicked() {
        var pass = true
        if (!validateTitle(titleLiveData.value!!))
            pass = false
        if (!validateDetails(detailsLiveData.value!!))
            pass = false
        if (!validatePrice(priceLiveData.value!!))
            if (!validateCategory(categoryLiveData.value!!))
                pass = false

        if (pass)
            startEditingService(true)
    }

    private val _editServiceResponse = MutableLiveData<Resource>(Resource.Idle)
    val editServiceResponse: LiveData<Resource> get() = _editServiceResponse

    fun editService() {

        viewModelScope.launch {
            val map = mutableMapOf<String, String>()
            map["id"] = service!!.serviceId.toString()
            map["title"] = titleLiveData.value!!
            map["description"] = detailsLiveData.value!!
            map["user_id"] = UserInfo.userId.toString()

            val categories =
                (_categoriesResponse.value!! as Resource.Success<MutableList<Category>>).data
            map["category_id"] = categories.find {
                it.name == categoryLiveData.value!!
            }?.categoryId.toString()

            map["price"] = priceLiveData.value!!

            try {
                _editServiceResponse.value = Resource.Loading
                progressState.value = Resource.Loading
                val apiResponse = repo.editService(map)
                if (apiResponse.status) {
                    _editServiceResponse.value = Resource.Success(apiResponse.message)
                } else {
                    _editServiceResponse.value = Resource.Failed(apiResponse.message)
                }
                progressState.value = Resource.Idle
            } catch (e: Exception) {
                Log.d(TAG, "AddService-Req. Failed: $e")
                progressState.value = Resource.Idle
                _editServiceResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException, is SocketTimeoutException -> app.getString(R.string.no_internet_connection)
        else -> app.getString(R.string.something_went_wrong_try_again_later)
    }

    private fun validateTitle(title: String) = if (title.isEmpty()) {
        titleErrorLiveData.value = app.getString(R.string.empty_field_error_message)
        false
    } else {
        if (title.length < 3) {
            titleErrorLiveData.value =
                app.getString(R.string.service_title_length_error_message)
            false
        } else
            true
    }

    private fun validateDetails(details: String) = if (details.isEmpty()) {
        detailsErrorLiveData.value =
            app.getString(R.string.empty_field_error_message)
        false
    } else {
        true
    }

    private fun validatePrice(price: String) = if (price.isEmpty()) {
        priceErrorLiveData.value =
            app.getString(R.string.empty_field_error_message)
        false
    } else {
        true
    }

    private fun validateCategory(category: String) = if (category.isEmpty()) {
        categoryErrorLiveData.value =
            app.getString(R.string.empty_spinner_field_error_message)
        false
    } else {
        true
    }
}