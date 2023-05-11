package code_grow.dohahub.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.dohahub.app.R
import code_grow.dohahub.app.model.Message
import code_grow.dohahub.app.repository.ChatRepository
import code_grow.dohahub.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException

private const val TAG = "ChatViewModel"

class ChatViewModel(
    private val application: Application,
    private val userId: Int,
    private val receiverId: Int,
    private val repo: ChatRepository
) : ViewModel() {
    private val _startRequestMessages = MutableLiveData(false)
    val startRequestMessages: LiveData<Boolean> get() = _startRequestMessages

    private val _startSendMessage = MutableLiveData(false)
    val startSendMessage: LiveData<Boolean> get() = _startSendMessage

    fun startRequestMessages(value: Boolean) {
        _startRequestMessages.value = value
    }

    fun startSendMessage(value: Boolean) {
        _startSendMessage.value = value
    }

    private val _messagesResponse = MutableLiveData<Resource>(Resource.Idle)
    val messagesResponse: LiveData<Resource> get() = _messagesResponse

    private val _sendMessageResponse = MutableLiveData<Resource>(Resource.Idle)
    val sendMessageResponse: LiveData<Resource> get() = _sendMessageResponse

    init {
        startRequestMessages(true)
    }

    fun getMessages() {
        viewModelScope.launch {
            try {
                _messagesResponse.value = Resource.Loading
                val apiResponse = repo.fetchMessages(userId, receiverId)
                if (apiResponse.status) {
                    val data = apiResponse.data as MutableList<Message>
                    if (data.isEmpty())
                        _messagesResponse.value = Resource.Empty
                    else
                        _messagesResponse.value = Resource.Success(data)
                } else {
                    _messagesResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Chat-Req. Failed: $e")
                _messagesResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    val messageLiveData = MutableLiveData("")
    fun sendMessageBtnClicked() {
        if (messageLiveData.value!!.isEmpty())
            return

        startSendMessage(true)
    }

    fun sendMessage() {
        viewModelScope.launch {
            try {
                _sendMessageResponse.value = Resource.Loading
                val apiResponse = repo.sendMessage(userId, receiverId, messageLiveData.value!!)
                if (apiResponse.status) {
                    val data = apiResponse.data as MutableList<Message>
                    if (data.isEmpty())
                        _sendMessageResponse.value = Resource.Empty
                    else {
                        _sendMessageResponse.value = Resource.Idle
                        _messagesResponse.value = Resource.Success(data)
                    }
                    messageLiveData.value = ""
                } else {
                    _sendMessageResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Chat-Req. Failed: $e")
                _sendMessageResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException, is SocketTimeoutException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }
}