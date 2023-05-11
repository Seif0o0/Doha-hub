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
import code_grow.dohahub.app.model.City
import code_grow.dohahub.app.model.Product
import code_grow.dohahub.app.model.ProductCategory
import code_grow.dohahub.app.repository.ProductRepository
import code_grow.dohahub.app.retrofit.Resource
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.SocketTimeoutException

private const val TAG = "EditProductViewModel"

class EditProductViewModel(
    private val product: Product,
    private val application: Application,
    private val repo: ProductRepository
) : ViewModel() {
    /* productTitle input */
    val productTitleLiveData = MutableLiveData(product.name)

    /* productTitle error message */
    val productTitleErrorLiveData = MutableLiveData("")

    /* details input */
    val detailsLiveData = MutableLiveData(product.details)

    /* details error message */
    val detailsErrorLiveData = MutableLiveData("")

    /* price input */
    val priceLiveData = MutableLiveData("${product.price}")

    /* price error message */
    val priceErrorLiveData = MutableLiveData("")

    /* category input */
    val categoryLiveData = MutableLiveData("")

    /* category error message */
    val categoryErrorLiveData = MutableLiveData("")

    /* city input */
    val cityLiveData = MutableLiveData("")

    /* city error message */
    val cityErrorLiveData = MutableLiveData("")

    /* phone input */
    val phoneLiveData = MutableLiveData(product.phoneNumber)

    /* whatsApp input */
    val whatsAppLiveData = MutableLiveData(product.whatsApp)

    /* address input */
    val addressLiveData = MutableLiveData(product.address)

    private val _startEditingProductBooleanLiveData = MutableLiveData(false)
    val startEditingProductBooleanLiveData: LiveData<Boolean> get() = _startEditingProductBooleanLiveData

    fun startEditingProduct(value: Boolean) {
        _startEditingProductBooleanLiveData.value = value
    }

    private val _citiesResponse = MutableLiveData<Resource>(Resource.Idle)
    val citiesResponse: LiveData<Resource> get() = _citiesResponse

    val startRequestCitiesLiveData = MutableLiveData(false)

    fun startRequestCities(value: Boolean) {
        startRequestCitiesLiveData.value = value
    }

    private val _categoriesResponse = MutableLiveData<Resource>(Resource.Idle)
    val categoriesResponse: LiveData<Resource> get() = _categoriesResponse

    val startRequestCategoriesLiveData = MutableLiveData(false)

    fun startRequestCategories(value: Boolean) {
        startRequestCategoriesLiveData.value = value
    }

    val progressStatus = MutableLiveData<Resource>(Resource.Idle)

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

    fun getCities() {
        viewModelScope.launch {
            try {
                _citiesResponse.value = Resource.Loading
                val apiResponse = repo.fetchCities()
                if (apiResponse.status) {
                    _citiesResponse.value =
                        Resource.Success(apiResponse.cities)
                } else {
                    _citiesResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "cities-Req. Failed: $e")
                _citiesResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    fun updateBtnClicked() {
        var pass = true
        if (_uploadImageResponse.value is Resource.Loading) {
            imageMessageLiveData.value =
                application.getString(R.string.wait_uploading_photo_message)
            pass = false
        }
        if (!validateTitle(productTitleLiveData.value!!))
            pass = false
        if (!validateDetails(detailsLiveData.value!!))
            pass = false
        if (!validatePrice(priceLiveData.value!!))
            pass = false
        if (!validateCity(cityLiveData.value!!))
            pass = false
        if (!validateCategory(categoryLiveData.value!!))
            pass = false

        if (pass)
            startEditingProduct(true)
    }

    private val _startDeletingProduct = MutableLiveData(false)
    val startDeletingProduct: LiveData<Boolean> get() = _startDeletingProduct

    fun startDeletingProduct(value: Boolean) {
        _startDeletingProduct.value = value
    }

    private val _deleteProductResponse = MutableLiveData<Resource>(Resource.Idle)
    val deleteProductResponse: LiveData<Resource> get() = _deleteProductResponse

    fun deleteProduct() {
        viewModelScope.launch {
            val map = mutableMapOf<String, String>()
            map["id"] = product.productId.toString()
            map["user_id"] = UserInfo.userId.toString()

            try {
                progressStatus.value = Resource.Loading
                _deleteProductResponse.value = Resource.Loading
                val apiResponse = repo.deleteProduct(map)
                if (apiResponse.status) {
                    _deleteProductResponse.value = Resource.Success(apiResponse.message)
                } else {
                    _deleteProductResponse.value = Resource.Failed(apiResponse.message)
                }
                progressStatus.value = Resource.Idle
            } catch (e: Exception) {
                Log.d(TAG, "EditProduct-Req. Failed: $e")
                progressStatus.value = Resource.Idle
                _deleteProductResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private val _editProductResponse = MutableLiveData<Resource>(Resource.Idle)
    val editProductResponse: LiveData<Resource> get() = _editProductResponse

    fun editProduct() {
        viewModelScope.launch {
            val map = mutableMapOf<String, String>()
            map["user_id"] = UserInfo.userId.toString()
            map["id"] = product.productId.toString()
            map["name"] = productTitleLiveData.value!!
            map["body"] = detailsLiveData.value!!

            val cities = (_citiesResponse.value!! as Resource.Success<MutableList<City>>).data
            map["city_id"] = cities.find {
                it.name == cityLiveData.value!!
            }?.cityId.toString()

            map["user_id"] = UserInfo.userId.toString()

            val categories =
                (_categoriesResponse.value!! as Resource.Success<MutableList<ProductCategory>>).data
            map["store_id"] = categories.find {
                it.name == categoryLiveData.value!!
            }?.categoryId.toString()

            map["price"] = priceLiveData.value!!
            if (phoneLiveData.value!!.isNotEmpty())
                map["mobile"] = phoneLiveData.value!!
            if (whatsAppLiveData.value!!.isNotEmpty())
                map["whatsapp"] = whatsAppLiveData.value!!
            if (addressLiveData.value!!.isNotEmpty())
                map["address"] = addressLiveData.value!!

            if (uploadedImage.isNotEmpty())
                map["image"] = uploadedImage
            else{
                if (product.productPhoto.isNotEmpty())
                    map["image"] = product.productPhoto.split("product_img/")[1]
            }

            try {
                _editProductResponse.value = Resource.Loading
                val apiResponse = repo.editProduct(map)
                if (apiResponse.status) {
                    _editProductResponse.value = Resource.Success(apiResponse.message)
                } else {
                    _editProductResponse.value = Resource.Failed(apiResponse.message)
                }
            } catch (e: Exception) {
                Log.d(TAG, "AddProduct-Req. Failed: $e")
                _editProductResponse.value = Resource.Failed(catchNetworkException(e))
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

    var uploadedImage =
        if (product.productPhoto.isEmpty()) "" else product.productPhoto.split("product_img/")[1]
    private val _uploadImageResponse =
        MutableLiveData<Resource>(Resource.Success(if (product.productPhoto.isEmpty()) "" else product.productPhoto.split("product_img/")[1]))
    val uploadImageResponse: LiveData<Resource> get() = _uploadImageResponse
    val imageMessageLiveData = MutableLiveData("")
    var cancelUploadingImage =
        false /* if user tried to remove his image while it getting uploaded */
    private lateinit var file: File

    /*
        Start uploading report using MultiPart request
        (uploadedImage = "") in case if user uploaded image successfully before and decided to change it before editing the product. so if the second one failed we have to reset the last one
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
        productTitleErrorLiveData.value = application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (title.length < 3) {
            productTitleErrorLiveData.value =
                application.getString(R.string.product_title_length_error_message)
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

    private fun validateCity(city: String) = if (city.isEmpty()) {
        cityErrorLiveData.value =
            application.getString(R.string.empty_spinner_field_error_message)
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