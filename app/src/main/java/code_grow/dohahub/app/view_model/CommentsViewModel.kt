package code_grow.dohahub.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.dohahub.app.R
import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.model.Article
import code_grow.dohahub.app.model.Comment
import code_grow.dohahub.app.model.Message
import code_grow.dohahub.app.repository.ArticleRepository
import code_grow.dohahub.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException

private const val TAG = "CommentsViewModel"

class CommentsViewModel(
    private val application: Application,
    private val articleId: Int,
    private val repo: ArticleRepository
) : ViewModel() {

    private val _startRequestComments = MutableLiveData(false)
    val startRequestComments: LiveData<Boolean> get() = _startRequestComments

    private val _commentsResponse = MutableLiveData<Resource>(Resource.Idle)
    val commentsResponse: LiveData<Resource> get() = _commentsResponse

    var callApi = true
    fun startRequestComments(value: Boolean, callApi: Boolean) {
        _startRequestComments.value = value
        this.callApi = callApi
    }

    init {
        startRequestComments(value = true, callApi = true)
    }

    private val _startSendComment = MutableLiveData(false)
    val startSendComment: LiveData<Boolean> get() = _startSendComment


    fun startSendComment(value: Boolean) {
        _startSendComment.value = value
    }


    private val _sendCommentResponse = MutableLiveData<Resource>(Resource.Idle)
    val sendCommentResponse: LiveData<Resource> get() = _sendCommentResponse


    fun getComments() {
        viewModelScope.launch {
            try {
                _commentsResponse.value = Resource.Loading
                val apiResponse = repo.fetchComments(articleId)
                if (apiResponse.status) {
                    val data = apiResponse.data as MutableList<Comment>
                    if (data.isEmpty())
                        _commentsResponse.value = Resource.Empty
                    else
                        _commentsResponse.value = Resource.Success(data)
                } else {
                    _commentsResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Comments-Req. Failed: $e")
                _commentsResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    val commentLiveData = MutableLiveData("")
    fun addCommentBtnClicked() {
        if (commentLiveData.value!!.isEmpty())
            return
        startSendComment(true)
    }

    fun sendComment() {
        viewModelScope.launch {
            try {
                _sendCommentResponse.value = Resource.Loading
                val apiResponse =
                    repo.addComment(UserInfo.userId, articleId, commentLiveData.value!!)
                if (apiResponse.status) {
                    _sendCommentResponse.value = Resource.Success(apiResponse.message)
                    commentLiveData.value = ""
                } else {
                    _sendCommentResponse.value = Resource.Failed(apiResponse.message)
                }
            } catch (e: Exception) {
                Log.d(TAG, "AddComment-Req. Failed: $e")
                _sendCommentResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    fun addComment(comment: Comment) {
        startRequestComments(value = true, callApi = false)
        if (_commentsResponse.value is Resource.Empty) {
            _commentsResponse.value = Resource.Success(mutableListOf(comment));
        } else if (_commentsResponse.value is Resource.Success<*>) {
            val comments =
                (_commentsResponse.value as Resource.Success<*>).data as MutableList<Comment>
            comments.add(comment)
            _commentsResponse.value = Resource.Success(comments)
        }

    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException, is SocketTimeoutException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }
}