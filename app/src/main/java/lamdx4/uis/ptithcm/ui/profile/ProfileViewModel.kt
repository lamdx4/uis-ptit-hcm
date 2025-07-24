package lamdx4.uis.ptithcm.ui.profile

import lamdx4.uis.ptithcm.data.repository.StudentInfoRepository
import lamdx4.uis.ptithcm.data.model.CompleteStudentInfo
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val studentInfoRepository: StudentInfoRepository
) : ViewModel() {

    // Thêm hàm này để dùng cho flow mới: trả về profile, không update uiState
    suspend fun loadProfile(accessToken: String, maSV: String): CompleteStudentInfo? {
        return studentInfoRepository.getCompleteStudentInfo(accessToken, maSV)
    }
}