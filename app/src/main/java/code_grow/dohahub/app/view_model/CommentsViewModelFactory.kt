package code_grow.dohahub.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.dohahub.app.repository.ArticleRepository
import code_grow.dohahub.app.repository.ChatRepository

class CommentsViewModelFactory(
    private val application: Application,
    private val articleId: Int,
    private val repo: ArticleRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommentsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CommentsViewModel(application, articleId, repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}