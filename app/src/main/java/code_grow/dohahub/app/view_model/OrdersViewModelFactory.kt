package code_grow.dohahub.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.dohahub.app.repository.OrdersRepository

class OrdersViewModelFactory(
    private val isMyOrder : Boolean,
    private val app: Application,
    private val repo: OrdersRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrdersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrdersViewModel(isMyOrder,app, repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}
