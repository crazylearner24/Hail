package com.hail.app.data.repository
import com.hail.app.data.api.ApiService
import com.hail.app.data.model.*
import com.hail.app.session.SessionManager
import com.hail.app.util.*

class AuthRepository(private val api: ApiService, private val session: SessionManager) {
    // TODO: if the real login requires RSA-encrypted credentials, run them through CryptoManager here
    // (public key must come from the documented API, not be hardcoded).
    suspend fun login(user: String, pass: String): NetworkResult<Unit> {
        val r = safeCall { api.login(body = LoginRequest(user, pass)) }
        return when (r) {
            is NetworkResult.Success -> r.data.token?.let { session.save(it); NetworkResult.Success(Unit) }
                ?: NetworkResult.Error(r.data.message ?: "Login failed")
            is NetworkResult.Error -> r
            NetworkResult.Loading -> r
        }
    }
    fun logout() = session.clear()
}

class TestRepository(private val api: ApiService) {
    suspend fun assignedTests() = safeCall { api.quizzes().data.orEmpty() }
    suspend fun questions(testId: Int, studentId: Int) = safeCall { api.adaptiveTest(testId = testId, studentId = studentId).questions.orEmpty() }
    suspend fun submitAnswer(a: AnswerSubmission) = safeCall { api.submitAnswer(b = a) }
    /** Success only if the server explicitly says so; HTTP 200 alone is not treated as submitted. */
    suspend fun submitTest(testId: Int, auto: Boolean): NetworkResult<Unit> {
        val s = safeCall { api.submitTest(b = TestSubmission(testId, "TODO_VERIFY", auto)) }
        if (s !is NetworkResult.Success || s.data.status != true) return NetworkResult.Error(
            (s as? NetworkResult.Error)?.message ?: "Server did not confirm submission")
        val e = safeCall { api.endTest(b = EndTestRequest(testId)) }
        return if (e is NetworkResult.Success && e.data.status == true) NetworkResult.Success(Unit)
        else NetworkResult.Error((e as? NetworkResult.Error)?.message ?: "Server did not confirm end of test")
    }
}
