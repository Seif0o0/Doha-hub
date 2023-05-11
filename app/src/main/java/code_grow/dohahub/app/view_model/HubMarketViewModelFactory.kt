package code_grow.dohahub.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.dohahub.app.repository.HomeRepository
import code_grow.dohahub.app.repository.ProductRepository

class HubMarketViewModelFactory(
    private val app: Application,
    private val repo: ProductRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MarketHubViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MarketHubViewModel(app, repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}