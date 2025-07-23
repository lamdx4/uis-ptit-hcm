package lamdx4.uis.ptithcm.ui.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    navController: NavController? = null,
    modifier: Modifier = Modifier
) {
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
            // Chức năng chính được chuyển từ navigation bar
            ListItem(
                headlineContent = { Text("Lịch thi") },
                leadingContent = { Icon(Icons.Default.Event, contentDescription = null) },
                modifier = Modifier.clickable { 
                    navController?.navigate("exam") 
                }
            )
            ListItem(
                headlineContent = { Text("Đăng ký môn học") },
                leadingContent = { Icon(Icons.Default.Edit, contentDescription = null) },
                modifier = Modifier.clickable { 
                    navController?.navigate("register") 
                }
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Các chức năng khác
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