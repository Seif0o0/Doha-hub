package code_grow.dohahub.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code_grow.dohahub.app.R
import code_grow.dohahub.app.model.Article
import code_grow.dohahub.app.model.Product
import code_grow.dohahub.app.model.ProductCategory
import code_grow.dohahub.app.repository.ArticleRepository
import code_grow.dohahub.app.retrofit.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException

private const val TAG = "ArticlesViewModel"

class ArticlesViewModel(
    val application: Application,
    val repo: ArticleRepository
) : ViewModel() {

    private val _startRequestArticles = MutableLiveData(false)
    val startRequestArticles: LiveData<Boolean> get() = _startRequestArticles

    private val _articlesResponse = MutableLiveData<Resource>(Resource.Idle)
    val articlesResponse: LiveData<Resource> get() = _articlesResponse

    fun startRequestArticles(value: Boolean) {
        _startRequestArticles.value = value
    }

    private val articlesQueryMap = mutableMapOf<String, String>()

    init {
        startRequestArticles(true)
    }

    fun getArticles() {
        viewModelScope.launch {
            try {
                _articlesResponse.value = Resource.Loading
                val apiResponse = repo.fetchArticles(articlesQueryMap)
                if (apiResponse.status) {
                    val data = apiResponse.data as MutableList<Article>
                    if (data.isEmpty())
                        _articlesResponse.value = Resource.Empty
                    else
                        _articlesResponse.value = Resource.Success(data)
                } else {
                    _articlesResponse.value = Resource.Failed(apiResponse.message!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Articles-Req. Failed: $e")
                _articlesResponse.value = Resource.Failed(catchNetworkException(e))
            }
        }
    }

    private fun catchNetworkException(e: Exception) = when (e) {
        is IOException, is SocketTimeoutException -> application.getString(R.string.no_internet_connection)
        else -> application.getString(R.string.something_went_wrong_try_again_later)
    }

    fun startSearching(query: String) {
        if (query.isNotEmpty())
            articlesQueryMap["search"] = query
        else
            articlesQueryMap.remove("search")

        startRequestArticles(true)
    }

}