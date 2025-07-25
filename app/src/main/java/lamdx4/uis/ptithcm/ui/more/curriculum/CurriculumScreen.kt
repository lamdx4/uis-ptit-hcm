package lamdx4.uis.ptithcm.ui.more.curriculum

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import lamdx4.uis.ptithcm.common.activityViewModel
import lamdx4.uis.ptithcm.data.model.Course
import lamdx4.uis.ptithcm.data.model.SemesterProgram
import lamdx4.uis.ptithcm.ui.AppViewModel
import lamdx4.uis.ptithcm.ui.theme.PTITColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurriculumScreen(
    appViewModel: AppViewModel = activityViewModel(),
    curriculumViewModel: CurriculumViewModel = hiltViewModel(),
    navController: NavController? = null,
    modifier: Modifier = Modifier
) {
    val userState by appViewModel.uiState.collectAsState()

    val curriculumTypes by curriculumViewModel.curriculumTypeState.collectAsState()
    val curriculumResponse by curriculumViewModel.curriculumState.collectAsState()

    var selectedTypeIndex by remember { mutableIntStateOf(0) }
    var expanded by remember { mutableStateOf(false) } // Dropdown state

    LaunchedEffect(Unit) {
            curriculumViewModel.loadCurriculumTypes()
            curriculumViewModel.loadCurriculums(programType = 1)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chương trình đào tạo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        val curriculumData = curriculumResponse?.data
        if (curriculumData == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // --- Dropdown chọn loại curriculum ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        curriculumTypes.getOrNull(selectedTypeIndex)?.description
                            ?: "Chọn loại CTĐT"
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    curriculumTypes.forEachIndexed { index, type ->
                        DropdownMenuItem(
                            text = { Text(type.description) },
                            onClick = {
                                expanded = false
                                selectedTypeIndex = index
                                curriculumViewModel.loadCurriculums(
                                    programType = type.value
                                )
                            }
                        )
                    }
                }
            }

            // --- Nội dung curriculum ---
            val semesterPrograms: List<SemesterProgram> = curriculumData.semesterPrograms

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(
                    top = 16.dp,
                    bottom = padding.calculateBottomPadding() + 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(semesterPrograms) { semester ->
                    SemesterCard(semester = semester)
                }
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
private fun SemesterCard(semester: SemesterProgram) {
    val totalCredits = semester.courses.sumOf { it.credit.toIntOrNull() ?: 0 }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = semester.semesterName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PTITColors.redDefault.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "$totalCredits TC",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = PTITColors.redDefault,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                semester.courses.forEach { course ->
                    SubjectRow(course)
                }
            }
        }
    }
}

@Composable
private fun SubjectRow(course: Course) {
    var expanded by remember { mutableStateOf(false) } // Trạng thái mở rộng

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { expanded = !expanded }, // Toggle khi nhấn
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // --- Hàng thông tin cơ bản ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = course.courseName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = course.courseCode,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val isLearned = course.completedCourse == "x"
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isLearned) PTITColors.success.copy(alpha = 0.1f) else PTITColors.warning.copy(
                            alpha = 0.1f
                        )
                    ) {
                        Text(
                            text = if (isLearned) "Đã học" else "Chưa học",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isLearned) PTITColors.success else PTITColors.warning,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "${course.credit} TC",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // --- Phần mở rộng ---
            if (expanded) {
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Tổng tiết: ${course.totalHours.takeIf { it.isNotBlank() } ?: "0"}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Lý thuyết: ${course.theoryHours.takeIf { it.isNotBlank() } ?: "0"}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Thực hành: ${course.practiceHours.takeIf { it.isNotBlank() } ?: "0"}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

        }
    }
}
