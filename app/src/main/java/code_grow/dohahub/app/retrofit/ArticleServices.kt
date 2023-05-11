package code_grow.dohahub.app.retrofit

import code_grow.dohahub.app.model.*
import code_grow.dohahub.app.utils.Constants
import okhttp3.MultipartBody
import retrofit2.http.*

interface ArticleServices {

    @GET(Constants.ARTICLE.plus(Constants.ARTICLES))
    suspend fun fetchArticles(@QueryMap queryMap: Map<String, String>): ApiResponse<MutableList<Article>>

    @Multipart
    @POST(Constants.ARTICLE.plus(Constants.UPLOAD_ARTICLE_IMAGE))
    suspend fun uploadImage(@Part body: MultipartBody.Part): UploadFilesResponse<MutableList<String>>

    @GET(Constants.ARTICLE.plus(Constants.ADD_ARTICLE))
    suspend fun addNewArticle(@QueryMap queryMap: Map<String, String>): MessageApiResponse

    @GET(Constants.ARTICLE.plus(Constants.EDIT_ARTICLE))
    suspend fun editArticle(@QueryMap queryMap: Map<String, String>): MessageApiResponse

    @GET(Constants.ARTICLE.plus(Constants.DELETE_ARTICLE))
    suspend fun deleteArticle(@QueryMap queryMap: Map<String, String>): MessageApiResponse

    @GET(Constants.ARTICLE.plus(Constants.COMMENTS))
    suspend fun fetchComments(@Query("id") articleId: Int): ApiResponse<MutableList<Comment>>

    @GET(Constants.ARTICLE.plus(Constants.ADD_COMMENT))
    suspend fun addComment(
        @Query("user_id") userId: Int,
        @Query("blog_id") articleId: Int,
        @Query("comment") comment: String
    ): MessageApiResponse

}