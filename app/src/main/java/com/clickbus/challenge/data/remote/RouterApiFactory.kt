package com.clickbus.challenge.data.remote

import com.clickbus.challenge.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

object RouterApiFactory {
    private const val DEFAULT_TIMEOUT_SECONDS = 10L

    // ignoreUnknownKeys: se a API ganhar campos novos, o app antigo continua funcionando.
    private val json = Json { ignoreUnknownKeys = true }

    fun create(
        baseUrl: String = BuildConfig.API_BASE_URL,
        timeoutSeconds: Long = DEFAULT_TIMEOUT_SECONDS,
    ): RouterApi {
        val client = OkHttpClient.Builder()
            .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
                }
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(RouterApi::class.java)
    }
}
