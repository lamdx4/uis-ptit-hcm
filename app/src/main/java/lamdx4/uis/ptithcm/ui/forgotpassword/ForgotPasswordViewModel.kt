package lamdx4.uis.ptithcm.ui.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _forgotPasswordUiState = MutableStateFlow(ForgotPasswordUiState())
    val forgotPasswordUiState: StateFlow<ForgotPasswordUiState> =
        _forgotPasswordUiState.asStateFlow()

    private val _resetPasswordUiState = MutableStateFlow(ResetPasswordUiState())
    val resetPasswordUiState: StateFlow<ResetPasswordUiState> = _resetPasswordUiState.asStateFlow()

    // Store studentId and email temporarily for the reset process
    private var currentStudentId: String = ""
    private var currentEmail: String = ""

    fun requestOTP(studentId: String, email: String) {
        if (studentId.isBlank() || email.isBlank()) {
            _forgotPasswordUiState.value = _forgotPasswordUiState.value.copy(
                errorMessage = "Vui lòng nhập đầy đủ thông tin."
            )
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _forgotPasswordUiState.value = _forgotPasswordUiState.value.copy(
                errorMessage = "Email không hợp lệ."
            )
            return
        }
        currentStudentId = studentId
        currentEmail = email
        _forgotPasswordUiState.value = _forgotPasswordUiState.value.copy(
            isLoading = true,
            errorMessage = null,
            isOtpSent = false
        )
        viewModelScope.launch {
            val result = authRepository.forgotPassword(studentId, email)
            if (result.isSuccess) {
                _forgotPasswordUiState.value = _forgotPasswordUiState.value.copy(
                    isLoading = false,
                    isOtpSent = true,
                    successMessage = result.getOrNull() ?: "OTP đã được gửi thành công."
                )
            } else {
                _forgotPasswordUiState.value = _forgotPasswordUiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message
                        ?: "Không thể gửi OTP. Vui lòng thử lại."
                )
            }
        }
    }

    fun resetPassword(otp: String, confirmPassword: String, newPassword: String) {
        if (otp.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            _resetPasswordUiState.value = _resetPasswordUiState.value.copy(
                errorMessage = "Vui lòng nhập đầy đủ thông tin."
            )
            return
        }
        if (newPassword != confirmPassword) {
            _resetPasswordUiState.value = _resetPasswordUiState.value.copy(
                errorMessage = "Mật khẩu mới và xác nhận mật khẩu không khớp."
            )
            return
        }
        _resetPasswordUiState.value = _resetPasswordUiState.value.copy(
            isLoading = true,
            errorMessage = null,
            isPasswordResetSuccess = false
        )
        viewModelScope.launch {
            val result = authRepository.resetPassword(currentStudentId, otp, newPassword)
            if (result.isSuccess) {
                _resetPasswordUiState.value = _resetPasswordUiState.value.copy(
                    isLoading = false,
                    isPasswordResetSuccess = true,
                    successMessage = result.getOrNull()
                )
            } else {
                _resetPasswordUiState.value = _resetPasswordUiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Đặt lại mật khẩu thất bại."
                )
            }
        }
    }

    fun clearForgotPasswordState() {
        _forgotPasswordUiState.value = ForgotPasswordUiState()
        currentStudentId = ""
        currentEmail = ""
    }

    fun clearResetPasswordState() {
        _resetPasswordUiState.value = ResetPasswordUiState()
    }
}

data class ForgotPasswordUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isOtpSent: Boolean = false
)

data class ResetPasswordUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isPasswordResetSuccess: Boolean = false
)
