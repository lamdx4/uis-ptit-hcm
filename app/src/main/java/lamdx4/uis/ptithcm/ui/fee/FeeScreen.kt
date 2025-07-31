package lamdx4.uis.ptithcm.ui.fee

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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