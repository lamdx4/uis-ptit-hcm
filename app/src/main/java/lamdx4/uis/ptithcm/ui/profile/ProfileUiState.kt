package lamdx4.uis.ptithcm.ui.profile

import lamdx4.uis.ptithcm.data.model.CompleteStudentInfo

// Deprecated: ProfileUiState không còn được sử dụng
// Thông tin profile được quản lý trong AppViewModel
// Thông tin thống kê được quản lý trong StatisticsViewModel
data class ProfileUiState(
    val studentInfo: CompleteStudentInfo? = null,
    val loading: Boolean = false,
    val error: String? = null
)