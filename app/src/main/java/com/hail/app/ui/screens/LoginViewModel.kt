package com.hail.app.ui.screens
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hail.app.data.repository.AuthRepository
import com.hail.app.util.NetworkResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LoginUiState(val loading: Boolean = false, val error: String? = null, val done: Boolean = false)

class LoginViewModel(private val repo: AuthRepository) : ViewModel() {
    private val _s = MutableStateFlow(LoginUiState()); val state = _s.asStateFlow()
    fun login(u: String, p: String) {
        if (_s.value.loading || u.isBlank() || p.isBlank()) return
        _s.value = LoginUiState(loading = true)
        viewModelScope.launch {
            _s.value = when (val r = repo.login(u.trim(), p)) {
                is NetworkResult.Success -> LoginUiState(done = true)
                is NetworkResult.Error -> LoginUiState(error = r.message)
                else -> LoginUiState()
            }
        }
    }
}
