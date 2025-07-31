package lamdx4.uis.ptithcm.ui.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import lamdx4.uis.ptithcm.common.activityViewModel
import lamdx4.uis.ptithcm.ui.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    navController: NavController? = null,
    modifier: Modifier
) {
    val appViewModel = activityViewModel<AppViewModel>()
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header cho chức năng chính
            item {
                Text(
                    text = "Chức năng chính",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Chức năng chính được chuyển từ navigation bar
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    ListItem(
                        headlineContent = { Text("Thông tin chi tiết") },
                        supportingContent = { Text("Xem đầy đủ thông tin cá nhân và học tập") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingContent = {
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        },
                        modifier = Modifier.clickable {
                            navController?.navigate("detailed_info")
                        }
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    ListItem(
                        headlineContent = { Text("Lịch thi") },
                        supportingContent = { Text("Xem lịch thi các môn học") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Event,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingContent = {
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        },
                        modifier = Modifier.clickable {
                            navController?.navigate("exam")
                        }
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    ListItem(
                        headlineContent = { Text("Đăng ký môn học") },
                        supportingContent = { Text("Đăng ký các môn học trong học kỳ") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingContent = {
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        },
                        modifier = Modifier.clickable {
                            navController?.navigate("register")
                        }
                    )
                }
            }

            // Spacer
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Header cho các chức năng khác
            item {
                Text(
                    text = "Tiện ích khác",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Danh sách các chức năng khác
            val otherFeatures = listOf(
                Triple(
                    "Học phí",
                    "Xem thông tin học phí và thanh toán",
                    Icons.Default.AttachMoney
                ) to "fee",
                Triple(
                    "Thông báo",
                    "Thông báo từ ban quản trị",
                    Icons.Default.Notifications
                ) to "notifications",
                Triple(
                    "Chương trình đào tạo",
                    "Xem chương trình đào tạo",
                    Icons.Default.MenuBook
                ) to "curriculum",
                Triple(
                    "Môn học tiên quyết",
                    "Xem môn học tiên quyết",
                    Icons.Default.AccountTree
                ) to "prerequisites",
                Triple(
                    "Hóa đơn điện tử",
                    "Quản lý hóa đơn điện tử",
                    Icons.Default.Receipt
                ) to "invoices",
                Triple(
                    "Cập nhật thông tin",
                    "Cập nhật thông tin thường trú",
                    Icons.Default.Edit
                ) to "update_info",
                Triple("Gửi ý kiến", "Gửi ý kiến ban quản trị", Icons.Default.Send) to "feedback",
                Triple(
                    "Đồng bộ Calendar",
                    "Đồng bộ với Google Calendar",
                    Icons.Default.CloudSync
                ) to "sync_calendar"
            )

            items(otherFeatures) { (feature, route) ->
                val (title, description, icon) = feature
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    ListItem(
                        headlineContent = { Text(title) },
                        supportingContent = { Text(description) },
                        leadingContent = {
                            Icon(
                                icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingContent = {
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        },
                        modifier = Modifier.clickable {
                            navController?.navigate(route)
                        }
                    )
                }
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    ListItem(
                        headlineContent = { Text("Đăng xuất") },
                        supportingContent = { Text("Thoát về màn hình đăng nhập") },
                        leadingContent = {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.clickable {
                            appViewModel.clearProfile()
                            appViewModel.clearLoginInfo()
                            navController?.navigate("login")
                        }
                    )
                }
            }
        }
    }
}