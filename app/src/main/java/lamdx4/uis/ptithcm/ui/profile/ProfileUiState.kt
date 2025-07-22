package lamdx4.uis.ptithcm.ui.profile

import lamdx4.uis.ptithcm.data.model.StudentInfoDetail

data class ProfileUiState(
    val studentInfo: StudentInfoDetail? = null,
    val loading: Boolean = false,
    val error: String? = null
)