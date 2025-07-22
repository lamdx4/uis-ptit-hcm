package lamdx4.uis.ptithcm.ui.fee

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeeScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Học phí") }) }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            Text("Thông tin học phí...", style = MaterialTheme.typography.titleMedium)
            // Thêm giao diện xem học phí ở đây
        }
    }
}