package lamdx4.uis.ptithcm.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.SessionManager
import lamdx4.uis.ptithcm.data.local.LoginPrefs
import lamdx4.uis.ptithcm.data.model.CompleteStudentInfo
import javax.inject.Inject

data class AppUserState(
    val maSV: String? = null,
    val profile: CompleteStudentInfo? = null
)

@HiltViewModel
class AppViewModel @Inject constructor(
    app: Application,
    private val sessionManager: SessionManager
) : AndroidViewModel(app) {

    private val _uiState = MutableStateFlow(AppUserState())
    val uiState: StateFlow<AppUserState> = _uiState

    fun saveLoginInfo(
        maSV: String,
    ) {
        _uiState.value = _uiState.value.copy(maSV = maSV)
    }

    fun clearLoginInfo() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(profile = null)
        }
    }


    fun setProfile(profile: CompleteStudentInfo) {
        _uiState.value = _uiState.value.copy(profile = profile)
    }

    fun logout() {
        this.clearLoginInfo()
        this.sessionManager.logout()
    }
}