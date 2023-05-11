package code_grow.dohahub.app.retrofit

sealed class Resource {
    data class Success<T>(val data: T) : Resource()
    data class Failed(val message: String) : Resource()
    object Loading : Resource()
    object Idle : Resource()
    object Empty : Resource()
}