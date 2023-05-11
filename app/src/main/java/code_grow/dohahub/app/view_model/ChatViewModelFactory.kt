package code_grow.dohahub.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.dohahub.app.repository.ChatRepository

class ChatViewModelFactory(
    private val application: Application,
    private val userId: Int,
    private val receiverId: Int,
    private val repo: ChatRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(application, userId, receiverId, repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}