package code_grow.dohahub.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.dohahub.app.repository.HomeRepository
import code_grow.dohahub.app.repository.MyServicesRepository

class MyServicesViewModelFactory(
    private val app: Application,
    private val repo: MyServicesRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyServicesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyServicesViewModel(app, repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}
