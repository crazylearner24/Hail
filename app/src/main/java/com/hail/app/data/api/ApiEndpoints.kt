package com.hail.app.data.api

/** Confirmed host/path pairs only. Do not guess the rest. */
object ApiEndpoints {
    private const val AP = "execute-api.ap-south-1.amazonaws.com"
    const val ADAPTIVE_QUIZZES = "https://g5yrbrzz1i.$AP/adaptive/getQuizzes"
    const val ADAPTIVE_TEST_COUNT = "https://gyep8eszlj.$AP/adaptive/getTestCount"
    const val ADAPTIVE_GET_TEST = "https://rma2yovfd6.$AP/adaptiveGetTest"
    const val PROCTOR_EXAM = "https://qtvwob5970.$AP/adaptive/proctorExam"
    const val END_TEST = "https://ak9s4kzi0c.$AP/adaptive/endTest"
    const val MCQ_TEST_SUBMISSION = "https://vkowkw6j4k.$AP/adaptiveMcqTestSubmission"
    const val LEGACY_TOKEN = "https://ycngba0n0j.$AP/auth/getLegacyToken"
    const val RESET_PASSWORD = "https://z0c0fzo9mj.$AP/auth/resetPassword"
    const val MCQ_QUESTION_SUBMISSION = "https://studentapp.edwisely.com/adaptiveMcqQuestionSubmission"

    // TODO(host unconfirmed): /auth/studentLogin, /auth/sendOtp, /user/v2/otpVerify, /auth/v2/refreshCache,
    //   /questionnaire/v3/updateSeenStatus, objective/*, questionnaireSubjective/*, codingAssessment/*, result endpoints.
    const val HOST_TODO = "https://TODO-CONFIRM-HOST"
    const val STUDENT_LOGIN = "$HOST_TODO/auth/studentLogin"
    const val UPDATE_SEEN_STATUS = "$HOST_TODO/questionnaire/v3/updateSeenStatus"

    // TODO(unresolved gateway IDs, paths unknown): 4c5ie0ug5h dmj2bmy0id gz2jn716ig 533031frr1 7d22tqyvtf
}
