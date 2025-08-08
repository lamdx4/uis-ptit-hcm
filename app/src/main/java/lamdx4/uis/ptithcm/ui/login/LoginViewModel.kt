package lamdx4.uis.ptithcm.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lamdx4.uis.ptithcm.data.local.LoginPrefs
import lamdx4.uis.ptithcm.data.repository.AuthRepository
import javax.inject.Inject

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val rememberMe: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val loginPrefs: LoginPrefs
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    init {
        viewModelScope.launch {
            combine(
                loginPrefs.username,
                loginPrefs.password,
                loginPrefs.rememberMe
            ) { username: String?, password: String?, rememberMe: Boolean ->
                LoginUiState(
                    username = username ?: "",
                    password = password ?: "",
                    rememberMe = rememberMe
                )
            }.collect { _uiState.value = it }
        }
    }


    suspend fun login() =
        withContext(Dispatchers.IO) {
            val username = _uiState.value.username
            val password = _uiState.value.password
            val rememberMe = _uiState.value.rememberMe

            if (username.isBlank() || password.isBlank()) {
                _uiState.update { it.copy(error = "Nhập đầy đủ tài khoản, mật khẩu!") }
                return@withContext Result.failure<Unit>(Exception("Nhập đầy đủ tài khoản, mật khẩu!"))
            }

            _uiState.update { it.copy(loading = true, error = null) }

            if (AuthRepository.TYPE_LOGIN == "SSO"){
                val result = authRepository.login2(username, password)
                result.onSuccess { res ->
                    _uiState.update { it.copy(loading = false, success = true, error = null) }
                    loginPrefs.saveLoginInfo(
                        res.accessToken,
                        "none",
                        res.userName,
                        res.userName,
                        password,
                        rememberMe
                    )
                    Result.success(Unit)
                }.onFailure { e ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            error = e.message ?: "Đăng nhập thất bại"
                        )
                    }
                    Result.failure<Unit>(e)
                }
            }
            else {
                val result = authRepository.login(username, password)
                result.onSuccess { res ->
                    _uiState.update { it.copy(loading = false, success = true, error = null) }
                    loginPrefs.saveLoginInfo(
                        res.accessToken,
                        res.refreshToken,
                        res.userName,
                        res.userName,
                        password,
                        rememberMe
                    )
                    Result.success(Unit)
                }.onFailure { e ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            error = e.message ?: "Đăng nhập thất bại"
                        )
                    }
                    Result.failure<Unit>(e)
                }
            }
        }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetLoginState() {
        _uiState.value = LoginUiState()
    }
    fun onRememberMeChange(newValue: Boolean) {
        _uiState.update { it.copy(rememberMe = newValue) }
    }
    fun updateUsername(newUsername: String) {
        _uiState.update { it.copy(username = newUsername) }
    }
    fun updatePassword(newPassword: String) {
        _uiState.update { it.copy(password = newPassword) }
    }
}