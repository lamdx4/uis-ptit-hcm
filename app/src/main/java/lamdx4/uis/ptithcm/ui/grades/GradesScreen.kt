package lamdx4.uis.ptithcm.ui.grades

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradesScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Kết quả học tập") }) }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize()) {
            items(5) { i ->
                Card(Modifier.padding(8.dp).fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text("Môn học $i") },
                        supportingContent = { Text("Điểm: ...") }
                    )
                }
            }
        }
    }
}