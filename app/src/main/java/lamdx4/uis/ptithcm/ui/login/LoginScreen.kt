package lamdx4.uis.ptithcm.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.ui.AppViewModel
import lamdx4.uis.ptithcm.data.repository.AuthRepository

@Composable
fun LoginScreen(
    appViewModel: AppViewModel,
    innerPadding: PaddingValues = PaddingValues(),
    onLoginSuccess: () -> Unit
) {
    val userState by appViewModel.uiState.collectAsState()
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var rememberMe by rememberSaveable { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }

    // Populate from rememberMe if available
    LaunchedEffect(userState.rememberMe) {
        if (userState.rememberMe) {
            username = userState.username.orEmpty()
            password = userState.password.orEmpty()
            rememberMe = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Đăng nhập", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Tài khoản") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mật khẩu") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it }
                    )
                    Text("Lưu tài khoản/mật khẩu")
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        loading = true
                        error = null
                        coroutineScope.launch {
                            val result = authRepository.login(username, password)
                            if (result.isSuccess) {
                                val accessToken = result.getOrNull() ?: ""
                                // Nếu API trả về mã sinh viên, lấy ở đây; nếu không thì để username (hoặc gọi API khác lấy mã SV)
                                val maSV = username
                                appViewModel.saveLoginInfo(
                                    accessToken = accessToken,
                                    maSV = maSV,
                                    username = username,
                                    password = password,
                                    rememberMe = rememberMe
                                )
                                loading = false
                                onLoginSuccess()
                            } else {
                                error = result.exceptionOrNull()?.message ?: "Sai tài khoản hoặc mật khẩu"
                                loading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loading
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Đăng nhập")
                    }
                }
                error?.let {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}