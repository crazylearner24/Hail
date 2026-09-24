package com.hail.app.ui.screens
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hail.app.data.model.*
import com.hail.app.data.repository.TestRepository
import com.hail.app.util.NetworkResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ExamUiState(
    val loading: Boolean = true, val error: String? = null, val questions: List<Question> = emptyList(),
    val index: Int = 0, val selected: Map<Int, Int> = emptyMap(), val seen: Set<Int> = emptySet(),
    val pending: Set<Int> = emptySet(), val remainingSec: Long = 0, val submitting: Boolean = false,
    val submitted: Boolean = false, val locked: Boolean = false)

class ExamViewModel(private val repo: TestRepository, private val testId: Int, private val studentId: Int,
                    durationSec: Long) : ViewModel() {
    private val _s = MutableStateFlow(ExamUiState(remainingSec = durationSec)); val state = _s.asStateFlow()
    private val spent = mutableMapOf<Int, Long>(); private var qStart = System.currentTimeMillis()
    private var timer: Job? = null

    init { load() }
    private fun load() = viewModelScope.launch {
        when (val r = repo.questions(testId, studentId)) {
            is NetworkResult.Success -> { _s.update { it.copy(loading = false, questions = r.data) }; markSeen(0); startTimer() }
            is NetworkResult.Error -> _s.update { it.copy(loading = false, error = r.message) }
            else -> {}
        }
    }
    private fun startTimer() { timer?.cancel(); timer = viewModelScope.launch {
        while (_s.value.remainingSec > 0) { delay(1000); _s.update { it.copy(remainingSec = it.remainingSec - 1) } }
        submit(auto = true) } }

    fun goTo(i: Int) { val q = _s.value.questions; if (_s.value.locked || i !in q.indices) return
        val cur = q[_s.value.index].id; spent[cur] = (spent[cur] ?: 0) + (System.currentTimeMillis() - qStart)
        qStart = System.currentTimeMillis(); _s.update { it.copy(index = i) }; markSeen(i) }
    private fun markSeen(i: Int) { val id = _s.value.questions.getOrNull(i)?.id ?: return
        if (id in _s.value.seen) return; _s.update { it.copy(seen = it.seen + id) }
        // TODO: POST updateSeenStatus once its host is confirmed (dedupe already handled above).
    }
    fun select(q: Question, optionId: Int) { if (_s.value.locked) return
        _s.update { it.copy(selected = it.selected + (q.id to optionId), pending = it.pending + q.id) } // instant UI
        viewModelScope.launch {
            val a = AnswerSubmission(testId, q.id, optionId, (spent[q.id] ?: 0) / 1000)
            if (repo.submitAnswer(a) is NetworkResult.Success) _s.update { it.copy(pending = it.pending - q.id) }
            // on failure stays in `pending`; TODO: persist in Room and retry on connectivity return
        } }
    fun submit(auto: Boolean = false) { if (_s.value.submitting || _s.value.submitted) return
        _s.update { it.copy(submitting = true, locked = true) }
        viewModelScope.launch {
            when (val r = repo.submitTest(testId, auto)) {
                is NetworkResult.Success -> _s.update { it.copy(submitting = false, submitted = true) }
                is NetworkResult.Error -> _s.update { it.copy(submitting = false, error = r.message, locked = auto) }
                else -> {}
            } } }
}
