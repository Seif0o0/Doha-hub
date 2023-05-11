package code_grow.dohahub.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.dohahub.app.R
import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.model.OrderResponse
import code_grow.dohahub.app.model.Product
import code_grow.dohahub.app.model.User
import code_grow.dohahub.app.repository.OrdersRepository
import code_grow.dohahub.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException

private const val TAG = "OrdersViewModel"

class OrdersViewModel(
    private val isMyOrder: Boolean,
    val application: Application,
    val repo: OrdersRepository
) : ViewModel() {
    private val _startRequestOrders = MutableLiveData(false)
    val startRequestOrders: LiveData<Boolean> get() = _startRequestOrders

    private val _ordersResponse = MutableLiveData<Resource>(Resource.Idle)
    val ordersResponse: LiveData<Resource> get() = _ordersResponse


    fun startRequestOrders(value: Boolean) {
        _startRequestOrders.value = value
    }

    init {
        startRequestOrders(true)
    }

    fun getOrders() {
        val map = mutableMapOf<String, Int>()
        if (isMyOrder)
            map["customer_id"] = UserInfo.userId
        else/* client orders */
            map["user_id"] = UserInfo.userId

        viewModelScope.launch {
            try {
                _ordersResponse.value = Resource.Loading
                val apiResponse = repo.fetchOrders(map)
                if (apiResponse.status) {
                    val data = apiResponse.data as MutableList<OrderResponse>
                    if (data.isEmpty())
                        _ordersResponse.value = Resource.Empty
                    else
                        _ordersResponse.value = Resource.Success(data)
                } else {
                    _ordersResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Orders-Req. Failed: $e")
                _ordersResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException, is SocketTimeoutException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }
}