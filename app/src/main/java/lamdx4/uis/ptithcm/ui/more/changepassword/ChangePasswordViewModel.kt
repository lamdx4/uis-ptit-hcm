package lamdx4.uis.ptithcm.ui.more.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.local.LoginPrefs
import lamdx4.uis.ptithcm.data.repository.AuthRepository

data class ChangePasswordUiState(
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val oldPasswordError: String? = null,
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null,
    val isSubmitting: Boolean = false,
    val enableSubmit: Boolean = false
)

sealed interface ChangePasswordUiEvent {
    data class ShowMessage(val message: String) : ChangePasswordUiEvent
    data class Success(val message: String) : ChangePasswordUiEvent
}

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val loginPrefs: LoginPrefs
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    // SharedFlow thay cho Channel
    private val _events = MutableSharedFlow<ChangePasswordUiEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val events = _events // Flow<ChangePasswordUiEvent>

    fun onOldPasswordChange(value: String) {
        updateState { copy(oldPassword = value) }
        reValidateRealtime()
    }

    fun onNewPasswordChange(value: String) {
        updateState { copy(newPassword = value) }
        reValidateRealtime()
    }

    fun onConfirmPasswordChange(value: String) {
        updateState { copy(confirmPassword = value) }
        reValidateRealtime()
    }

    fun submit(studentId: String) {
        if (!validateAll()) return

        viewModelScope.launch {
            updateState { copy(isSubmitting = true) }
            val newPassword = _uiState.value.newPassword
            val r = repo.changePassword(
                username = studentId,
                oldPassword = _uiState.value.oldPassword,
                newPassword = newPassword
            )
            // reset form
            _uiState.value = ChangePasswordUiState()
            r.onSuccess {
                val result = repo.login2(studentId, newPassword)
                result.onSuccess { res ->
                    loginPrefs.saveAccessToken(res.accessToken)
                    _events.tryEmit(ChangePasswordUiEvent.Success("Đổi mật khẩu thành công"))
                }.onFailure { e ->
                    _events.tryEmit(ChangePasswordUiEvent.Success("Đổi mật khẩu thành công"))
                    loginPrefs.deleteAllDataLogin()
                }
            }.onFailure {
                _events.tryEmit(ChangePasswordUiEvent.ShowMessage(it.message ?: "Không xác định"))
            }
            updateState { copy(isSubmitting = false) }
        }
    }

    private fun reValidateRealtime() {
        val s = _uiState.value
        val enable = s.oldPassword.isNotBlank() &&
                s.newPassword.isNotBlank() &&
                s.confirmPassword.isNotBlank()
        updateState { copy(enableSubmit = enable && !isSubmitting) }
    }

    private fun validateAll(): Boolean {
        val s = _uiState.value
        var oldErr: String? = null
        var newErr: String? = null
        var confirmErr: String? = null

        if (s.oldPassword.isBlank()) oldErr = "Không được để trống"

        if (s.newPassword.isBlank()) {
            newErr = "Không được để trống"
        } else {
            when {
                s.newPassword.length < 4 -> newErr = "Ít nhất 4 ký tự"
                s.newPassword == s.oldPassword && s.oldPassword.isNotBlank() -> newErr =
                    "Phải khác mật khẩu cũ"
            }
        }

        if (s.confirmPassword.isBlank()) {
            confirmErr = "Không được để trống"
        } else if (s.confirmPassword != s.newPassword) {
            confirmErr = "Không khớp mật khẩu mới"
        }

        val ok = oldErr == null && newErr == null && confirmErr == null

        updateState {
            copy(
                oldPasswordError = oldErr,
                newPasswordError = newErr,
                confirmPasswordError = confirmErr,
                enableSubmit = ok && !isSubmitting
            )
        }
        return ok
    }

    private inline fun updateState(block: ChangePasswordUiState.() -> ChangePasswordUiState) {
        _uiState.update(block)
    }
}

