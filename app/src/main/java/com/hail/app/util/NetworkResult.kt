package com.hail.app.util
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

sealed class NetworkResult<out T> {
    data object Loading : NetworkResult<Nothing>()
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val code: Int? = null) : NetworkResult<Nothing>()
}

suspend fun <T> safeCall(block: suspend () -> T): NetworkResult<T> = try {
    NetworkResult.Success(block())
} catch (e: CancellationException) { throw e
} catch (e: SocketTimeoutException) { NetworkResult.Error("Request timed out")
} catch (e: IOException) { NetworkResult.Error("No internet connection")
} catch (e: HttpException) {
    NetworkResult.Error(when (e.code()) {
        401 -> "Session expired"; 403 -> "Not allowed"; 404 -> "Not found"; 409 -> "Conflict with server state"
        422 -> "Invalid request"; 429 -> "Too many requests"; in 500..599 -> "Server error"; else -> "Unexpected error"
    }, e.code())
} catch (e: kotlinx.serialization.SerializationException) { NetworkResult.Error("Unexpected server response")
} catch (e: Exception) { NetworkResult.Error("Something went wrong") }
