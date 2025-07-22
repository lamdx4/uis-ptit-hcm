package lamdx4.uis.ptithcm.ui.profile

import StudentRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class ProfileViewModel(
    private val studentRepository: StudentRepository = StudentRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    fun loadStudentInfo(accessToken: String, maSV: String) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState(loading = true)
            try {
                val res = studentRepository.getStudentInfo(accessToken, maSV)
                if (res.result) {
                    _uiState.value = ProfileUiState(studentInfo = res.data.thong_tin_sinh_vien)
                } else {
                    _uiState.value = ProfileUiState(error = "Không tìm thấy thông tin sinh viên")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState(error = e.message)
            }
        }
    }
}