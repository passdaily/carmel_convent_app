package info.passdaily.camrelconvertapp.services.retrofit

import info.passdaily.camrelconvertapp.services.Utils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient() {

    companion object {


        private val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        private val client : OkHttpClient = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
        }.build()

        fun create() : ApiInterface {

            var oktHttpClient = OkHttpClient.Builder()
                ///use this timeout future
//                .callTimeout(2, TimeUnit.MINUTES)
//                .connectTimeout(20, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
             //   .addInterceptor(NetworkConnectionInterceptor(context))
                .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                    val request = chain.request()
                        .newBuilder()
                        .addHeader("content-Type", "application/json;charset=utf-8")
                        .addHeader("Accept", "application/json")
                        .build()
                    chain.proceed(request)
                })


            val retrofit = Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(ApiInterface.BASE_URL)
                .client(oktHttpClient.build())
                .build()
            return retrofit.create(ApiInterface::class.java)
        }

        private fun getClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
//                .addInterceptor(HttpLoggingInterceptor().setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE)
                .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                    val request = chain.request()
                        .newBuilder()
                        .addHeader("content-Type", "application/json;charset=utf-8")
                        .addHeader("Accept", "application/json")
                        .build()
                    chain.proceed(request)
                }).build()
        }
    }
}

