package lamdx4.uis.ptithcm.ui.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import lamdx4.uis.ptithcm.data.model.CompleteStudentInfo
import lamdx4.uis.ptithcm.data.repository.ScheduleRepository
import lamdx4.uis.ptithcm.data.repository.StudentInfoRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val studentInfoRepository: StudentInfoRepository,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    // Thêm hàm này để dùng cho flow mới: trả về profile, không update uiState
    suspend fun loadProfile(maSV: String): CompleteStudentInfo? {
        // Save schedule to the database as soon as log in.
        val currentSemester = scheduleRepository.getCurrentSemester()
        scheduleRepository.saveWeeklySchedule(currentSemester?.semesterCode ?: 20243)

        return studentInfoRepository.getCompleteStudentInfo( maSV)
    }
}