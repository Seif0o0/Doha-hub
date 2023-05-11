package code_grow.dohahub.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.dohahub.app.repository.OrdersRepository

class ClientOrderDetailsViewModelFactory(
    private val customerId: Int,
    private val orderId: Int,
    private val app: Application,
    private val repo: OrdersRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClientOrderDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClientOrderDetailsViewModel(customerId, orderId, app, repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}
