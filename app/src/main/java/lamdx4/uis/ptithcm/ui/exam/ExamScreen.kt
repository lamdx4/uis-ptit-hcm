package lamdx4.uis.ptithcm.ui.exam

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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