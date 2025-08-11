package lamdx4.uis.ptithcm.ui.more.changepassword

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.common.activityViewModel
import lamdx4.uis.ptithcm.ui.AppViewModel

@Composable
fun ChangePasswordScreen(
    modifier: Modifier = Modifier,
    viewModel: ChangePasswordViewModel = hiltViewModel(),
    onSuccess: (() -> Unit)? = null,
    navController: NavHostController
) {
    var studentId = remember { "" }
    val appViewModel = activityViewModel<AppViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var showOld by remember { mutableStateOf(false) }
    var showNew by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    // Collect one-shot events
    LaunchedEffect(Unit) {
        viewModel.events.collect { e ->
            when (e) {
                is ChangePasswordUiEvent.ShowMessage -> {
                    scope.launch { snackbarHostState.showSnackbar(e.message) }
                }

                is ChangePasswordUiEvent.Success -> {
                    scope.launch { snackbarHostState.showSnackbar(e.message) }
                    onSuccess?.invoke()
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        appViewModel.uiState.collect {
            studentId = it.maSV.toString()
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    "Đổi mật khẩu",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Vui lòng nhập mật khẩu cũ và đặt mật khẩu mới của bạn.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(24.dp))

                // Old password
                OutlinedPasswordField(
                    value = uiState.oldPassword,
                    onValueChange = viewModel::onOldPasswordChange,
                    label = "Mật khẩu cũ",
                    show = showOld,
                    onToggleShow = { showOld = !showOld },
                    error = uiState.oldPasswordError,
                    imeAction = ImeAction.Next,
                    onIme = { focusManager.moveFocus(FocusDirection.Down) }
                )
                Spacer(Modifier.height(16.dp))

                // New password
                OutlinedPasswordField(
                    value = uiState.newPassword,
                    onValueChange = viewModel::onNewPasswordChange,
                    label = "Mật khẩu mới",
                    show = showNew,
                    onToggleShow = { showNew = !showNew },
                    error = uiState.newPasswordError,
                    imeAction = ImeAction.Next,
                    onIme = { focusManager.moveFocus(FocusDirection.Down) }
                )
                Spacer(Modifier.height(16.dp))

                // Confirm password
                OutlinedPasswordField(
                    value = uiState.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChange,
                    label = "Xác nhận mật khẩu mới",
                    show = showConfirm,
                    onToggleShow = { showConfirm = !showConfirm },
                    error = uiState.confirmPasswordError,
                    imeAction = ImeAction.Done,
                    onIme = {
                        focusManager.clearFocus()
                        if (uiState.enableSubmit && !uiState.isSubmitting) {
                            viewModel.submit(studentId)
                        }
                    }
                )

                Spacer(Modifier.height(28.dp))

                Button(
                    onClick = { viewModel.submit(studentId) },
                    enabled = uiState.enableSubmit && !uiState.isSubmitting,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Đang xử lý...")
                    } else {
                        Icon(Icons.Filled.Check, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Đổi mật khẩu")
                    }
                }

                Spacer(Modifier.height(40.dp))
            }

            // SnackbarHost không cần Scaffold – tự đặt bottom
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                snackbar = { data ->
                    Snackbar(
                        action = {
                            // có thể thêm nút đóng nếu muốn
                        }
                    ) { Text(data.visuals.message) }
                }
            )
        }
    }
}

@Composable
private fun OutlinedPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    show: Boolean,
    onToggleShow: () -> Unit,
    error: String?,
    imeAction: ImeAction,
    onIme: () -> Unit
) {
    Column {
        androidx.compose.material3.OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            isError = error != null,
            trailingIcon = {
                IconButton(onClick = onToggleShow) {
                    Icon(
                        imageVector = if (show) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (show) "Ẩn" else "Hiện"
                    )
                }
            },
            visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onAny = { onIme() }
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        AnimatedVisibility(visible = error != null) {
            Text(
                text = error ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}