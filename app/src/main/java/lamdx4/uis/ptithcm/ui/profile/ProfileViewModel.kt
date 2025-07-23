package lamdx4.uis.ptithcm.ui.profile

import lamdx4.uis.ptithcm.data.repository.StudentRepository
import lamdx4.uis.ptithcm.data.model.CompleteStudentInfo
import androidx.lifecycle.ViewModel


class ProfileViewModel(
    private val studentRepository: StudentRepository = StudentRepository()
) : ViewModel() {

    // Thêm hàm này để dùng cho flow mới: trả về profile, không update uiState
    suspend fun loadProfile(accessToken: String, maSV: String): CompleteStudentInfo? {
        return studentRepository.getCompleteStudentInfo(accessToken, maSV)
    }
}