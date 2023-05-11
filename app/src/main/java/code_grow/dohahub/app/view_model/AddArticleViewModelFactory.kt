package code_grow.dohahub.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.dohahub.app.repository.ArticleRepository
import code_grow.dohahub.app.repository.AuthRepository
import code_grow.dohahub.app.repository.ProductRepository

class AddArticleViewModelFactory(
    private val app: Application,
    private val repo: ArticleRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddArticleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddArticleViewModel(app, repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}
