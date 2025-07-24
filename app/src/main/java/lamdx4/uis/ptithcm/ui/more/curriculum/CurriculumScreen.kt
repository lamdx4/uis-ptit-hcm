package lamdx4.uis.ptithcm.ui.more.curriculum

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import lamdx4.uis.ptithcm.ui.AppViewModel
import lamdx4.uis.ptithcm.ui.theme.PTITColors

data class Subject(
    val code: String,
    val name: String,
    val credits: Int,
    val semester: Int,
    val isRequired: Boolean = true,
    val prerequisites: List<String> = emptyList()
)

data class SemesterGroup(
    val semester: Int,
    val subjects: List<Subject>,
    val totalCredits: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurriculumScreen(
    appViewModel: AppViewModel = hiltViewModel(),
    navController: NavController? = null,
    modifier: Modifier = Modifier
) {
    // Sample curriculum data
    val curriculum = remember {
        listOf(
            SemesterGroup(
                semester = 1,
                subjects = listOf(
                    Subject("TONH1001", "Toán cao cấp 1", 3, 1),
                    Subject("VLYH1001", "Vật lý đại cương 1", 3, 1),
                    Subject("CNTT1001", "Tin học đại cương", 3, 1),
                    Subject("GDQP1001", "Giáo dục quốc phòng 1", 2, 1),
                    Subject("ANVA1001", "Tiếng Anh 1", 3, 1),
                    Subject("DDLL1001", "Pháp luật đại cương", 2, 1)
                ),
                totalCredits = 16
            ),
            SemesterGroup(
                semester = 2,
                subjects = listOf(
                    Subject("TONH1002", "Toán cao cấp 2", 3, 2, prerequisites = listOf("TONH1001")),
                    Subject("VLYH1002", "Vật lý đại cương 2", 3, 2, prerequisites = listOf("VLYH1001")),
                    Subject("CNTT1002", "Lập trình căn bản", 3, 2, prerequisites = listOf("CNTT1001")),
                    Subject("GDQP1002", "Giáo dục quốc phòng 2", 2, 2, prerequisites = listOf("GDQP1001")),
                    Subject("ANVA1002", "Tiếng Anh 2", 3, 2, prerequisites = listOf("ANVA1001")),
                    Subject("DDLL1002", "Triết học Mác-Lênin", 3, 2)
                ),
                totalCredits = 17
            ),
            SemesterGroup(
                semester = 3,
                subjects = listOf(
                    Subject("TONH2001", "Toán rời rạc", 3, 3),
                    Subject("CNTT2001", "Cấu trúc dữ liệu và giải thuật", 3, 3, prerequisites = listOf("CNTT1002")),
                    Subject("CNTT2002", "Lập trình hướng đối tượng", 3, 3, prerequisites = listOf("CNTT1002")),
                    Subject("ANVA2001", "Tiếng Anh 3", 3, 3, prerequisites = listOf("ANVA1002")),
                    Subject("DDLL2001", "Kinh tế chính trị Mác-Lênin", 2, 3),
                    Subject("CNTT2003", "Kiến trúc máy tính", 3, 3)
                ),
                totalCredits = 17
            ),
            SemesterGroup(
                semester = 4,
                subjects = listOf(
                    Subject("CNTT2004", "Cơ sở dữ liệu", 3, 4),
                    Subject("CNTT2005", "Mạng máy tính", 3, 4),
                    Subject("CNTT2006", "Hệ điều hành", 3, 4),
                    Subject("CNTT2007", "Công nghệ phần mềm", 3, 4, prerequisites = listOf("CNTT2001")),
                    Subject("DDLL2002", "Chủ nghĩa xã hội khoa học", 2, 4),
                    Subject("CNTT2008", "Phân tích thiết kế thuật toán", 3, 4, prerequisites = listOf("CNTT2001"))
                ),
                totalCredits = 17
            )
        )
    }

    val totalCredits = curriculum.sumOf { it.totalCredits }
    val userState by appViewModel.uiState.collectAsState()
    val accessToken = userState.accessToken

    LaunchedEffect(accessToken) {
        if (accessToken != null) {

        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Chương trình đào tạo",
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
                                Icons.Default.MenuBook,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Column {
                                Text(
                                    text = "Ngành Công nghệ thông tin",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Bậc đại học - 4 năm",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                        
                        HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$totalCredits",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Tổng tín chỉ",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${curriculum.size}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Học kỳ",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${curriculum.sumOf { it.subjects.size }}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Môn học",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            items(curriculum) { semesterGroup ->
                SemesterCard(semesterGroup = semesterGroup)
            }
        }
    }
}

@Composable
private fun SemesterCard(semesterGroup: SemesterGroup) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Semester header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = PTITColors.redDefault.copy(alpha = 0.1f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${semesterGroup.semester}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PTITColors.redDefault
                            )
                        }
                    }
                    
                    Text(
                        text = "Học kỳ ${semesterGroup.semester}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PTITColors.redDefault.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "${semesterGroup.totalCredits} TC",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = PTITColors.redDefault,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            
            // Subjects list
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                semesterGroup.subjects.forEach { subject ->
                    SubjectRow(subject = subject)
                }
            }
        }
    }
}

@Composable
private fun SubjectRow(subject: Subject) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subject.code,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!subject.isRequired) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = PTITColors.warning.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "Tự chọn",
                                style = MaterialTheme.typography.labelSmall,
                                color = PTITColors.warning,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    
                    Text(
                        text = "${subject.credits} TC",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            if (subject.prerequisites.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.AccountTree,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tiên quyết: ${subject.prerequisites.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
