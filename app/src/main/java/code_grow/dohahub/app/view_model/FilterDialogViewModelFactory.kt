package code_grow.dohahub.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.dohahub.app.repository.HomeRepository

class FilterDialogViewModelFactory(
    private val app: Application,
    private val repo: HomeRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FilterDialogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FilterDialogViewModel(app, repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}