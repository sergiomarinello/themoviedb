package com.smarinello.themoviedb.repository.remote

import com.smarinello.themoviedb.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val CONNECT_TIMEOUT_SECONDS = 10L
private const val READ_TIMEOUT_SECONDS = 10L

/**
 * Remote server used to build the [OkHttpClient] to request data.
 */
class RemoteAccess {

    /**
     * [Retrofit] instance used to request data.
     */
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .client(httpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().apply {
            connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            addInterceptor { chain ->
                chain.proceed(
                    chain.request().run {
                        newBuilder().url(getHttpUrl(this)).build()
                    }
                )
            }
            addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
        }.build()
    }

    private fun getHttpUrl(request: Request) =
        request.url.newBuilder().setEncodedQueryParameter(
            BuildConfig.API_KEY_PARAM_NAME,
            BuildConfig.API_KEY_VALUE
        ).build()
}
