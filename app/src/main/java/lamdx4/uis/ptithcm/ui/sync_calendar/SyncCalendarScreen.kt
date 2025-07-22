package lamdx4.uis.ptithcm.ui.sync_calendar

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncCalendarScreen(onSyncSuccess: () -> Unit = {}) {
    val context = LocalContext.current
    var selectedAccount by remember { mutableStateOf<GoogleSignInAccount?>(null) }
    var syncing by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestScopes(com.google.android.gms.common.api.Scope("https://www.googleapis.com/auth/calendar"))
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.result
                selectedAccount = account
                // Tiến hành đồng bộ sau khi chọn tài khoản
                // Gọi function thực hiện sync ở đây
            } catch (e: Exception) {
                errorMsg = "Lỗi chọn tài khoản Google: ${e.message}"
            }
        } else {
            errorMsg = "Hủy chọn tài khoản"
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Đồng bộ lịch UIS với Google Calendar") }) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    launcher.launch(signInIntent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Chọn tài khoản Google để đồng bộ")
            }
            selectedAccount?.let {
                Spacer(Modifier.height(16.dp))
                Text("Đã chọn: ${it.email}")
                // Khi đã chọn tài khoản, có thể show nút "Đồng bộ ngay"
                Button(
                    onClick = {
                        syncing = true
                        errorMsg = null
                        // TODO: Gọi logic sync (lấy dữ liệu thời khóa biểu, tạo event Google Calendar)
                        // Sau khi xong:
                        syncing = false
                        onSyncSuccess()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !syncing
                ) {
                    Text("Đồng bộ lịch học lên Google Calendar")
                }
            }
            if (syncing) {
                Spacer(Modifier.height(16.dp))
                CircularProgressIndicator()
            }
            errorMsg?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}