package com.hail.app.data.api
import com.hail.app.session.SessionManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import java.util.concurrent.TimeUnit
import retrofit2.Retrofit

class AuthInterceptor(private val session: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val b = chain.request().newBuilder()
        // TODO: confirm the real auth header scheme.
        session.token()?.let { b.header("Authorization", "Bearer $it") }
        return chain.proceed(b.build())
    }
}

object ApiClient {
    fun create(session: SessionManager): ApiService {
        val http = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(session)) // no logging interceptor on purpose: never log secrets
            .connectTimeout(15, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false) // never blind-retry exam POSTs
            .build()
        val json = Json { ignoreUnknownKeys = true; isLenient = true; explicitNulls = false }
        return Retrofit.Builder().baseUrl("https://localhost/").client(http)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build().create(ApiService::class.java)
    }
}
