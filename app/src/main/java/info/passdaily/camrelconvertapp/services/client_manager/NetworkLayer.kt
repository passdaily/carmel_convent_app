package info.passdaily.camrelconvertapp.services.client_manager

import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.retrofit.ApiInterface
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object NetworkLayer {
    ////ok http client
    var oktHttpClient = OkHttpClient.Builder()
//        .callTimeout(2, TimeUnit.MINUTES)
//        .connectTimeout(20, TimeUnit.SECONDS)
//        .readTimeout(30, TimeUnit.SECONDS)
//        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(Interceptor { chain: Interceptor.Chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("content-Type", "application/json;charset=utf-8")
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(request)
        })

    private val retrofit : Retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(ApiInterface.BASE_URL)
        .client(oktHttpClient.build())
       // .client(getLoggingHttpClient())
        .build()

    val services : ApiInterface by lazy { retrofit.create(ApiInterface::class.java) }

    val apiClient = ApiClient(services)



//    private fun getLoggingHttpClient(): OkHttpClient {
//        val builder = OkHttpClient.Builder()
//        builder.addInterceptor(HttpLoggingInterceptor().apply {
//            setLevel(HttpLoggingInterceptor.Level.BODY)
//        })
//
//        builder.addInterceptor(
//            ChuckerInterceptor.Builder(MyApplication.context)
//                .collector(ChuckerCollector(MyApplication.context))
//                .maxContentLength(250000L)
//                .redactHeaders(emptySet())
//                .alwaysReadResponseBody(false)
//                .build()
//        )
//
//        return builder.build()
//    }
}