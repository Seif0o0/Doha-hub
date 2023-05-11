package code_grow.dohahub.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.dohahub.app.repository.HomeRepository
import code_grow.dohahub.app.repository.ProductRepository

class ProviderDetailsViewModelFactory(
    private val providerId: Int,
    private val app: Application,
    private val repo: HomeRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProviderDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProviderDetailsViewModel(providerId, app, repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}