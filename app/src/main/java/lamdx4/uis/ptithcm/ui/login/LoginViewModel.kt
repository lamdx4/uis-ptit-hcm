package lamdx4.uis.ptithcm.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.repository.AuthRepository

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value) }
    }
    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun login() {
        val username = _uiState.value.username
        val password = _uiState.value.password
        if (username.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Nhập đầy đủ tài khoản, mật khẩu!") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            val result = authRepository.login(username, password)
            result.onSuccess { token ->
                // TODO: Lưu token vào DataStore/SecureStore nếu cần
                _uiState.update { it.copy(loading = false, success = true, error = null) }
            }.onFailure { e ->
                _uiState.update { it.copy(loading = false, error = e.message ?: "Đăng nhập thất bại") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetLoginState() {
        _uiState.value = LoginUiState()
    }
}