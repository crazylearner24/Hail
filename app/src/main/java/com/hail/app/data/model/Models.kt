package com.hail.app.data.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

// TODO: verify every field name against the real API contract; unknown fields are ignored, all nullable.
@Serializable data class LoginRequest(val username: String, val password: String)
@Serializable data class LoginResponse(val token: String? = null, val message: String? = null, val data: JsonElement? = null)
@Serializable data class TestItem(val id: Int? = null, val name: String? = null, val type: String? = null)
@Serializable data class QuizzesResponse(val data: List<TestItem>? = null)
@Serializable data class Option(val id: Int? = null, val text: String? = null)
@Serializable data class Question(val id: Int, val text: String? = null, val options: List<Option> = emptyList())
@Serializable data class TestQuestionsResponse(val questions: List<Question>? = null)
// Fields below are the ones the supplied mapping lists as confirmed.
@Serializable data class AnswerSubmission(
    @SerialName("test_id") val testId: Int, @SerialName("question_id") val questionId: Int,
    @SerialName("selected_option_id") val selectedOptionId: Int,
    @SerialName("time_spent_per_question_id") val timeSpent: Long)
@Serializable data class TestSubmission(
    @SerialName("test_id") val testId: Int, @SerialName("test_submission_type") val type: String,
    @SerialName("auto_submitted") val autoSubmitted: Boolean)
@Serializable data class EndTestRequest(@SerialName("test_id") val testId: Int)
@Serializable data class ApiAck(val status: Boolean? = null, val message: String? = null)
