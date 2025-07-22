package lamdx4.uis.ptithcm.ui.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen() {
    Scaffold(
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
                // Danh sách tuần (giả lập)
                LazyColumn {
                    items(3) { i ->
                        Card(Modifier.padding(8.dp).fillMaxWidth()) {
                            ListItem(
                                headlineContent = { Text("Tuần ${i + 1}: 01/01 - 07/01") },
                                supportingContent = { Text("Danh sách môn học tuần này...") }
                            )
                        }
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