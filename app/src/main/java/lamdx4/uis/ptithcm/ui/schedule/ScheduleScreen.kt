package lamdx4.uis.ptithcm.ui.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Thời khoá biểu") }) }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            // Tabs: Tuần/Học kỳ
            var selectedTab by remember { mutableStateOf(0) }
            val tabTitles = listOf("Dạng tuần", "Dạng học kỳ")
            TabRow(selectedTabIndex = selectedTab) {
                tabTitles.forEachIndexed { i, label ->
                    Tab(
                        selected = selectedTab == i,
                        onClick = { selectedTab = i },
                        text = { Text(label) }
                    )
                }
            }
            if (selectedTab == 0) {
                // Dạng tuần - Navigate to WeeklyScheduleScreen when tab is first selected
                LaunchedEffect(Unit) {
                    navController?.navigate("weekly_schedule")
                }
                // Show loading or placeholder while navigating
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Đang chuyển đến thời khoá biểu tuần...")
                    }
                }
            } else {
                // Dạng học kỳ (giả lập)
                LazyColumn {
                    items(1) {
                        Card(Modifier.padding(8.dp).fillMaxWidth()) {
                            ListItem(
                                headlineContent = { Text("Học kỳ 2024-2025") },
                                supportingContent = { Text("Danh sách môn học cả kỳ...") }
                            )
                        }
                    }
                }
            }
        }
    }
}