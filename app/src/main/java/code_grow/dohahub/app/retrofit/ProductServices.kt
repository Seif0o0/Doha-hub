package code_grow.dohahub.app.retrofit

import code_grow.dohahub.app.model.*
import code_grow.dohahub.app.utils.Constants
import okhttp3.MultipartBody
import retrofit2.http.*

interface ProductServices {
    @GET(Constants.PRODUCT.plus(Constants.PRODUCTS))
    suspend fun fetchProducts(@QueryMap queryMap: Map<String, String>): ApiResponse<MutableList<Product>>

    @GET(Constants.PRODUCT.plus(Constants.PRODUCT_CATEGORIES))
    suspend fun fetchProductsCategories(): ApiResponse<MutableList<ProductCategory>>

    @Multipart
    @POST(Constants.PRODUCT.plus(Constants.UPLOAD_PRODUCT_IMAGE))
    suspend fun uploadImage(@Part body: MultipartBody.Part): UploadFilesResponse<MutableList<String>>

    @GET(Constants.PRODUCT.plus(Constants.ADD_PRODUCT))
    suspend fun addNewProduct(@QueryMap queryMap: Map<String, String>): MessageApiResponse

    @GET(Constants.PRODUCT.plus(Constants.EDIT_PRODUCT))
    suspend fun editProduct(@QueryMap queryMap: Map<String, String>): MessageApiResponse

    @GET(Constants.PRODUCT.plus(Constants.DELETE_PRODUCT))
    suspend fun deleteProduct(@QueryMap queryMap: Map<String, String>): MessageApiResponse

}