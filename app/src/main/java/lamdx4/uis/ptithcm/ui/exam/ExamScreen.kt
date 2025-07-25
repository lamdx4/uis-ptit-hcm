package lamdx4.uis.ptithcm.ui.exam

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Lịch thi") }) }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize()) {
            items(3) { i ->
                Card(Modifier.padding(8.dp).fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text("Môn thi $i") },
                        supportingContent = { Text("Ngày: ... - Địa điểm: ...") }
                    )
                }
            }
        }
    }
}