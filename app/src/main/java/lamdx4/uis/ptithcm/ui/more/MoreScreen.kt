package lamdx4.uis.ptithcm.ui.more

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("More") }) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ListItem(
                headlineContent = { Text("Học phí") },
                leadingContent = { Icon(Icons.Default.AttachMoney, contentDescription = null) }
            )
            ListItem(
                headlineContent = { Text("Thông báo từ ban quản trị") },
                leadingContent = { Icon(Icons.Default.Info, contentDescription = null) }
            )
            ListItem(
                headlineContent = { Text("Xem chương trình đào tạo") },
                leadingContent = { Icon(Icons.Default.Info, contentDescription = null) }
            )
            ListItem(
                headlineContent = { Text("Xem môn học tiên quyết") },
                leadingContent = { Icon(Icons.Default.Info, contentDescription = null) }
            )
            ListItem(
                headlineContent = { Text("Hóa đơn điện tử") },
                leadingContent = { Icon(Icons.Default.Info, contentDescription = null) }
            )
            ListItem(
                headlineContent = { Text("Cập nhật thông tin thường trú") },
                leadingContent = { Icon(Icons.Default.Info, contentDescription = null) }
            )
            ListItem(
                headlineContent = { Text("Gửi ý kiến ban quản trị") },
                leadingContent = { Icon(Icons.Default.Send, contentDescription = null) }
            )
            ListItem(
                headlineContent = { Text("Đồng bộ với Google Calendar") },
                leadingContent = { Icon(Icons.Default.CloudSync, contentDescription = null) }
            )
        }
    }
}