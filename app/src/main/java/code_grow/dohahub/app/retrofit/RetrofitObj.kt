package code_grow.dohahub.app.retrofit

import code_grow.dohahub.app.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

fun httpClient(): OkHttpClient.Builder {
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY

    var httpClient = OkHttpClient.Builder()
//    if (BuildConfig.DEBUG)
    httpClient.addInterceptor(logging)

    return httpClient
}

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create())
    .baseUrl(Constants.BASE_URL)
    .client(httpClient().build())
    .build()

object AppApi {
    val globalServices: GlobalServices by lazy {
        retrofit.create(GlobalServices::class.java)
    }
    val productServices: ProductServices by lazy {
        retrofit.create(ProductServices::class.java)
    }
    val articleServices: ArticleServices by lazy {
        retrofit.create(ArticleServices::class.java)
    }
    val campaignServices: CampaignServices by lazy {
        retrofit.create(CampaignServices::class.java)
    }
    val serviceServices: ServiceServices by lazy {
        retrofit.create(ServiceServices::class.java)
    }
    val chatServices: ChatServices by lazy {
        retrofit.create(ChatServices::class.java)
    }
}


