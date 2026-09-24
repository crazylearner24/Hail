package com.hail.app.data.api
import com.hail.app.data.model.*
import retrofit2.http.*

interface ApiService {
    @POST suspend fun login(@Url url: String = ApiEndpoints.STUDENT_LOGIN, @Body body: LoginRequest): LoginResponse
    @GET suspend fun quizzes(@Url url: String = ApiEndpoints.ADAPTIVE_QUIZZES): QuizzesResponse
    // Request params per mapping: test_id, student_id. Response shape unverified.
    @GET suspend fun adaptiveTest(@Url url: String = ApiEndpoints.ADAPTIVE_GET_TEST,
        @Query("test_id") testId: Int, @Query("student_id") studentId: Int): TestQuestionsResponse
    @POST suspend fun submitAnswer(@Url url: String = ApiEndpoints.MCQ_QUESTION_SUBMISSION, @Body b: AnswerSubmission): ApiAck
    @POST suspend fun submitTest(@Url url: String = ApiEndpoints.MCQ_TEST_SUBMISSION, @Body b: TestSubmission): ApiAck
    @POST suspend fun endTest(@Url url: String = ApiEndpoints.END_TEST, @Body b: EndTestRequest): ApiAck
}
