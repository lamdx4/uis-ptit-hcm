package lamdx4.uis.ptithcm.ui.more.prerequisites

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import lamdx4.uis.ptithcm.ui.theme.PTITColors

data class PrerequisiteSubject(
    val code: String,
    val name: String,
    val credits: Int,
    val prerequisites: List<String>,
    val semester: Int,
    val isCompleted: Boolean = false,
    val grade: Double? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrerequisitesScreen(
    navController: NavController? = null,
    modifier: Modifier = Modifier
) {
    // Sample data với prerequisites phức tạp
    val subjects = remember {
        listOf(
            PrerequisiteSubject(
                code = "CNTT3001",
                name = "Lập trình Web",
                credits = 3,
                prerequisites = listOf("CNTT2002", "CNTT2004"),
                semester = 5,
                isCompleted = false
            ),
            PrerequisiteSubject(
                code = "CNTT3002", 
                name = "Lập trình Mobile",
                credits = 3,
                prerequisites = listOf("CNTT2002", "CNTT2001"),
                semester = 5,
                isCompleted = true,
                grade = 8.5
            ),
            PrerequisiteSubject(
                code = "CNTT3003",
                name = "Trí tuệ nhân tạo",
                credits = 3,
                prerequisites = listOf("CNTT2001", "CNTT2008", "TONH2001"),
                semester = 6,
                isCompleted = false
            ),
            PrerequisiteSubject(
                code = "CNTT3004",
                name = "Machine Learning",
                credits = 3,
                prerequisites = listOf("CNTT3003", "TONH1001", "TONH1002"),
                semester = 7,
                isCompleted = false
            ),
            PrerequisiteSubject(
                code = "CNTT4001",
                name = "Đồ án tốt nghiệp",
                credits = 10,
                prerequisites = listOf("CNTT2007", "CNTT3001", "CNTT3002"),
                semester = 8,
                isCompleted = false
            )
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    val filteredSubjects = subjects.filter { 
        it.name.contains(searchQuery, ignoreCase = true) || 
        it.code.contains(searchQuery, ignoreCase = true) 
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Môn học tiên quyết",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Tìm kiếm môn học") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
            }

            // Summary card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.AccountTree,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Column {
                                Text(
                                    text = "Cây môn học tiên quyết",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Theo dõi điều kiện học các môn",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                        
                        HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${subjects.count { it.isCompleted }}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PTITColors.success
                                )
                                Text(
                                    text = "Đã hoàn thành",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${subjects.count { !it.isCompleted }}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PTITColors.warning
                                )
                                Text(
                                    text = "Chưa học",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            // Legend
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Chú thích",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = PTITColors.success
                                )
                                Text(
                                    text = "Đã hoàn thành",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = PTITColors.warning
                                )
                                Text(
                                    text = "Chưa học",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            items(filteredSubjects) { subject ->
                PrerequisiteCard(subject = subject)
            }
        }
    }
}

@Composable
private fun PrerequisiteCard(subject: PrerequisiteSubject) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (subject.isCompleted) {
                PTITColors.successContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Subject header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            if (subject.isCompleted) Icons.Default.CheckCircle else Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = if (subject.isCompleted) PTITColors.success else PTITColors.warning
                        )
                        
                        Text(
                            text = subject.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    Text(
                        text = "${subject.code} • ${subject.credits} tín chỉ • Học kỳ ${subject.semester}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (subject.grade != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PTITColors.success.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "Điểm: ${subject.grade}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = PTITColors.success,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
            
            // Prerequisites
            if (subject.prerequisites.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Môn học tiên quyết:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        subject.prerequisites.forEach { prerequisite ->
                            PrerequisiteChip(prerequisite)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrerequisiteChip(prerequisiteCode: String) {
    // In thực tế, sẽ cần check xem môn này đã hoàn thành chưa
    val isCompleted = listOf("CNTT2002", "CNTT2004", "CNTT2001").contains(prerequisiteCode)
    
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isCompleted) {
            PTITColors.success.copy(alpha = 0.1f)
        } else {
            PTITColors.warning.copy(alpha = 0.1f)
        },
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCompleted) PTITColors.success.copy(alpha = 0.3f) else PTITColors.warning.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Schedule,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = if (isCompleted) PTITColors.success else PTITColors.warning
            )
            
            Text(
                text = prerequisiteCode,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = if (isCompleted) PTITColors.success else PTITColors.warning
            )
        }
    }
}
