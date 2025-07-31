package lamdx4.uis.ptithcm.ui.more.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import lamdx4.uis.ptithcm.ui.theme.PTITColors

data class NotificationItem(
    val id: String,
    val title: String,
    val content: String,
    val date: String,
    val isRead: Boolean = false,
    val priority: NotificationPriority = NotificationPriority.NORMAL
)

enum class NotificationPriority {
    HIGH, NORMAL, LOW
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    navController: NavController? = null,
    modifier: Modifier = Modifier
) {
    // Sample data - trong thực tế sẽ lấy từ API
    val notifications = remember {
        listOf(
            NotificationItem(
                id = "1",
                title = "Thông báo về lịch thi cuối kỳ",
                content = "Lịch thi cuối kỳ học kỳ 1 năm học 2024-2025 đã được cập nhật. Sinh viên vui lòng kiểm tra và chuẩn bị...",
                date = "23/07/2025",
                priority = NotificationPriority.HIGH
            ),
            NotificationItem(
                id = "2",
                title = "Thông báo nộp học phí",
                content = "Hạn nộp học phí học kỳ 1 năm học 2024-2025 là ngày 30/07/2025. Sinh viên chưa nộp học phí vui lòng...",
                date = "20/07/2025",
                isRead = true,
                priority = NotificationPriority.HIGH
            ),
            NotificationItem(
                id = "3",
                title = "Cập nhật hệ thống UIS",
                content = "Hệ thống sẽ được bảo trì từ 02:00 - 04:00 ngày 25/07/2025. Trong thời gian này, sinh viên không thể truy cập...",
                date = "19/07/2025",
                isRead = true
            ),
            NotificationItem(
                id = "4",
                title = "Hội thảo khoa học sinh viên",
                content = "Khoa Công nghệ thông tin tổ chức hội thảo khoa học sinh viên năm 2025. Hạn nộp bài tóm tắt là...",
                date = "18/07/2025"
            ),
            NotificationItem(
                id = "5",
                title = "Đăng ký môn học tự chọn",
                content = "Thời gian đăng ký môn học tự chọn từ ngày 01/08 - 05/08/2025. Sinh viên vui lòng đăng nhập hệ thống...",
                date = "17/07/2025",
                isRead = true
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Thông báo",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Mark all as read */ }) {
                        Icon(Icons.Default.DoneAll, contentDescription = "Mark all read")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                // Summary card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Column {
                            Text(
                                text = "${notifications.count { !it.isRead }} thông báo mới",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Tổng ${notifications.size} thông báo",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            items(notifications) { notification ->
                NotificationCard(
                    notification = notification,
                    onClick = {
                        // TODO: Navigate to notification detail or mark as read
                    }
                )
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notification: NotificationItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (notification.isRead) 2.dp else 4.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Priority indicator and title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (notification.priority == NotificationPriority.HIGH) {
                        Icon(
                            Icons.Default.PriorityHigh,
                            contentDescription = "High priority",
                            modifier = Modifier.size(16.dp),
                            tint = PTITColors.redDefault
                        )
                    }
                    
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .background(
                                        PTITColors.redDefault,
                                        shape = RoundedCornerShape(50.dp)
                                    )
                            )
                        }
                    }
                    
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold,
                        color = if (notification.isRead) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Text(
                    text = notification.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = notification.content,
                style = MaterialTheme.typography.bodyMedium,
                color = if (notification.isRead) {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                },
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
