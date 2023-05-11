package code_grow.dohahub.app.repository

import code_grow.dohahub.app.retrofit.AppApi
import okhttp3.MultipartBody

class ArticleRepository {
    suspend fun fetchArticles(map: Map<String, String>) = AppApi.articleServices.fetchArticles(map)
    suspend fun uploadImage(body: MultipartBody.Part) = AppApi.articleServices.uploadImage(body)
    suspend fun addNewArticle(map: Map<String, String>) = AppApi.articleServices.addNewArticle(map)
    suspend fun editArticle(map: Map<String, String>) = AppApi.articleServices.editArticle(map)
    suspend fun deleteArticle(map: Map<String, String>) = AppApi.articleServices.deleteArticle(map)
    suspend fun fetchComments(articleId:Int) = AppApi.articleServices.fetchComments(articleId)
    suspend fun addComment(userId:Int,articleId: Int,comment:String) = AppApi.articleServices.addComment(userId, articleId, comment)
}