package code_grow.dohahub.app.repository

import code_grow.dohahub.app.retrofit.AppApi
import okhttp3.MultipartBody

class ProductRepository {
    suspend fun fetchCategories() = AppApi.productServices.fetchProductsCategories()
    suspend fun fetchProducts(map: Map<String, String>) = AppApi.productServices.fetchProducts(map)
    suspend fun uploadImage(body: MultipartBody.Part) = AppApi.productServices.uploadImage(body)
    suspend fun addNewProduct(map: Map<String, String>) = AppApi.productServices.addNewProduct(map)
    suspend fun editProduct(map: Map<String, String>) = AppApi.productServices.editProduct(map)
    suspend fun deleteProduct(map: Map<String, String>) = AppApi.productServices.deleteProduct(map)
    suspend fun fetchCities() = AppApi.globalServices.fetchCities()
}