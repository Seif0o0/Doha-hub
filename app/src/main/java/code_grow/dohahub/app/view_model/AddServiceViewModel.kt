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
import code_grow.dohahub.app.model.ProductCategory
import code_grow.dohahub.app.repository.MyServicesRepository
import code_grow.dohahub.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException

private const val TAG = "AddServiceViewModel"

class AddServiceViewModel(
    private val application: Application,
    private val repo: MyServicesRepository
) : ViewModel() {
    /* title input */
    val titleLiveData = MutableLiveData("")

    /* title error message */
    val titleErrorLiveData = MutableLiveData("")

    /* details input */
    val detailsLiveData = MutableLiveData("")

    /* details error message */
    val detailsErrorLiveData = MutableLiveData("")

    /* price input */
    val priceLiveData = MutableLiveData("")

    /* price error message */
    val priceErrorLiveData = MutableLiveData("")

    /* category input */
    val categoryLiveData = MutableLiveData("")

    /* category error message */
    val categoryErrorLiveData = MutableLiveData("")

    private val _startAddingService = MutableLiveData(false)
    val startAddingService: LiveData<Boolean> get() = _startAddingService

    fun startAddingService(value: Boolean) {
        _startAddingService.value = value
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
                val apiResponse = repo.fetchCategories()
                if (apiResponse.status) {
                    _categoriesResponse.value = Resource.Success(apiResponse.data)
                } else {
                    _categoriesResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: java.lang.Exception) {
                Log.d(TAG, "Categories-Req. Failed: $e")
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
            startAddingService(true)
    }

    private val _addServiceResponse = MutableLiveData<Resource>(Resource.Idle)
    val addServiceResponse: LiveData<Resource> get() = _addServiceResponse

    fun addService() {
        viewModelScope.launch {
            val map = mutableMapOf<String, String>()
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
                _addServiceResponse.value = Resource.Loading
                val apiResponse = repo.addNewService(map)
                if (apiResponse.status) {
                    _addServiceResponse.value = Resource.Success(apiResponse.message)
                } else {
                    _addServiceResponse.value = Resource.Failed(apiResponse.message)
                }
            } catch (e: Exception) {
                Log.d(TAG, "AddService-Req. Failed: $e")
                _addServiceResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException, is SocketTimeoutException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

    private fun validateTitle(title: String) = if (title.isEmpty()) {
        titleErrorLiveData.value = application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (title.length < 3) {
            titleErrorLiveData.value =
                application.getString(R.string.service_title_length_error_message)
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


}