package code_grow.dohahub.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.dohahub.app.model.MyService
import code_grow.dohahub.app.model.Provider
import code_grow.dohahub.app.repository.MyServicesRepository
import code_grow.dohahub.app.repository.ProductRepository

class EditServiceViewModelFactory(
    private val service: MyService?,
    private val app: Application,
    private val repo: MyServicesRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditServiceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditServiceViewModel(service, app, repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}
