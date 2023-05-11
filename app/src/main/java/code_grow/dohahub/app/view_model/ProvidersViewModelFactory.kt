package code_grow.dohahub.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.dohahub.app.repository.HomeRepository

class ProvidersViewModelFactory(
    private val categoryId: Int,
    private val query: String,
    private val app: Application,
    private val repo: HomeRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProvidersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProvidersViewModel(categoryId, query, app, repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}
