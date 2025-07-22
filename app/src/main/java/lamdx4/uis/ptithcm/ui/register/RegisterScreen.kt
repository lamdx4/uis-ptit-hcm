package lamdx4.uis.ptithcm.ui.register

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Đăng ký môn") }) }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            Text("Đăng ký môn học, nguyện vọng...", style = MaterialTheme.typography.titleMedium)
            // Thêm giao diện đăng ký môn học ở đây
        }
    }
}