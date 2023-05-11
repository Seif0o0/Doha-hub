package code_grow.dohahub.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.dohahub.app.R
import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.model.Product
import code_grow.dohahub.app.model.ProductCategory
import code_grow.dohahub.app.repository.ProductRepository
import code_grow.dohahub.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException

private const val TAG = "MyProductsViewModel"
class MyProductsViewModel(
    val application: Application,
    val repo: ProductRepository
): ViewModel() {

    private val _startRequestCategories = MutableLiveData(false)
    val startRequestCategories: LiveData<Boolean> get() = _startRequestCategories

    private val _categoriesResponse = MutableLiveData<Resource>(Resource.Idle)
    val categoriesResponse: LiveData<Resource> get() = _categoriesResponse

    private val _startRequestProducts = MutableLiveData(false)
    val startRequestProducts: LiveData<Boolean> get() = _startRequestProducts

    private val _productsResponse = MutableLiveData<Resource>(Resource.Idle)
    val productsResponse: LiveData<Resource> get() = _productsResponse

    private val productsQueryMap = mutableMapOf<String, String>()

    fun startRequestProducts(value: Boolean) {
        _startRequestProducts.value = value
    }

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
                    val categories = apiResponse.data
                    categories?.add(
                        0,
                        ProductCategory(0, application.getString(R.string.all), true)
                    )
                    _categoriesResponse.value = Resource.Success(categories)
                } else {
                    _categoriesResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Categories-Req. Failed: $e")
                _categoriesResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    fun getProducts() {
        viewModelScope.launch {
            try {
                productsQueryMap["user_id"] = UserInfo.userId.toString()
                _productsResponse.value = Resource.Loading
                val apiResponse = repo.fetchProducts(productsQueryMap)

                if (apiResponse.status) {
                    val data = apiResponse.data as MutableList<Product>
                    if (data.isEmpty())
                        _productsResponse.value = Resource.Empty
                    else
                        _productsResponse.value = Resource.Success(data)
                } else {
                    _productsResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Products-Req. Failed: $e")
                _productsResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException, is SocketTimeoutException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

    fun startFilteringByCategory(categoryId: Int) {
        if (categoryId != 0)
            productsQueryMap["store_id"] = categoryId.toString()
        else {
            if (productsQueryMap.containsKey("store_id"))
                productsQueryMap.remove("store_id")
        }
        startRequestProducts(true)
    }
}