package code_grow.dohahub.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.dohahub.app.repository.AuthRepository

class ChangeForgetPasswordViewModelFactory(
    private val application: Application,
    private val repo: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChangeForgetPasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChangeForgetPasswordViewModel(application, repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}
