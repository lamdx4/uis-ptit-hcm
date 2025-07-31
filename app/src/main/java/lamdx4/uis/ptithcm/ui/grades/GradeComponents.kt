package lamdx4.uis.ptithcm.ui.grades

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import lamdx4.uis.ptithcm.data.model.SemesterGrade
import lamdx4.uis.ptithcm.data.model.SubjectGrade

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemesterSelector(
    semesters: List<SemesterGrade>,
    selectedSemester: SemesterGrade?,
    onSemesterSelected: (SemesterGrade) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedSemester?.semesterName ?: "Chọn học kỳ",
            onValueChange = {},
            readOnly = true,
            label = { Text("Học kỳ") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            semesters.forEach { semester ->
                DropdownMenuItem(
                    text = { Text(semester.semesterName) },
                    onClick = {
                        onSemesterSelected(semester)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun OverallGradeSummaryCard(semesters: List<SemesterGrade>) {
    val latestSemester = semesters.maxByOrNull { semester ->
        // Parse semester to determine the latest one based on name
        semester.semesterName
    }
    
    val totalCreditsAttempted = semesters.sumOf { 
        it.subjectGrades.sumOf { subject -> subject.credits.toIntOrNull() ?: 0 }
    }
    
    val totalCreditsPassed = semesters.sumOf { semester ->
        semester.subjectGrades.filter { it.result == 1 }
            .sumOf { subject -> subject.credits.toIntOrNull() ?: 0 }
    }
    
    val passedSubjects = semesters.sumOf { semester ->
        semester.subjectGrades.count { it.result == 1 }
    }
    
    val failedSubjects = semesters.sumOf { semester ->
        semester.subjectGrades.count { it.result == 0 }
    }
    
    val currentGpa10 = latestSemester?.cumulativeGpa10
    val currentGpa4 = latestSemester?.cumulativeGpa4
    val currentRank = latestSemester?.semesterRank
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "Kết quả tích lũy toàn khóa",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            
            // Main GPA Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItemLarge(
                    label = "GPA (10)",
                    value = currentGpa10 ?: "---",
                    color = getGpaColor(currentGpa10?.toDoubleOrNull()),
                    icon = Icons.Default.TrendingUp
                )
                StatisticItemLarge(
                    label = "GPA (4)",
                    value = currentGpa4 ?: "---",
                    color = getGpaColor(currentGpa4?.toDoubleOrNull()),
                    icon = Icons.Default.Grade
                )
                StatisticItemLarge(
                    label = "Xếp loại",
                    value = currentRank ?: "---",
                    color = getRankColor(currentRank),
                    icon = Icons.Default.EmojiEvents
                )
            }
            
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            
            // Credits and Subject Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    label = "Tín chỉ đạt",
                    value = "$totalCreditsPassed",
                    color = MaterialTheme.colorScheme.primary
                )
                StatisticItem(
                    label = "Tổng tín chỉ",
                    value = "$totalCreditsAttempted"
                )
                StatisticItem(
                    label = "Môn đạt",
                    value = "$passedSubjects",
                    color = Color(0xFF4CAF50)
                )
                StatisticItem(
                    label = "Môn rớt",
                    value = "$failedSubjects",
                    color = if (failedSubjects > 0) Color(0xFFF44336) else MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            
            // Progress indicator
            if (totalCreditsAttempted > 0) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tiến độ hoàn thành",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "${(totalCreditsPassed.toFloat() / totalCreditsAttempted * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    LinearProgressIndicator(
                        progress = { totalCreditsPassed.toFloat() / totalCreditsAttempted },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
fun StatisticItemLarge(
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}
    
@Composable
fun SemesterStatisticsCard(semester: SemesterGrade) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Kết quả học kỳ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatisticItem(
                    label = "GPA (10)",
                    value = semester.semesterGpa10 ?: "---",
                    color = getGpaColor(semester.semesterGpa10?.toDoubleOrNull())
                )
                StatisticItem(
                    label = "GPA (4)",
                    value = semester.semesterGpa4 ?: "---",
                    color = getGpaColor(semester.semesterGpa4?.toDoubleOrNull())
                )
                StatisticItem(
                    label = "Tín chỉ",
                    value = semester.creditsPassedSemester ?: "---"
                )
                StatisticItem(
                    label = "Xếp loại",
                    value = semester.semesterRank ?: "---",
                    color = getRankColor(semester.semesterRank)
                )
            }
            
            // Cumulative stats if available
            if (semester.cumulativeGpa10 != null) {
                Divider(modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    text = "Tích lũy: GPA ${semester.cumulativeGpa10} (${semester.cumulativeGpa4}) - ${semester.creditsPassedCumulative} tín chỉ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun StatisticItem(
    label: String, 
    value: String,
    color: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun SubjectGradeCard(
    subject: SubjectGrade,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = when (subject.result) {
                1 -> MaterialTheme.colorScheme.surfaceVariant // Passed
                0 -> MaterialTheme.colorScheme.errorContainer // Failed
                else -> MaterialTheme.colorScheme.surface // Not graded yet
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Subject header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = subject.subjectName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${subject.subjectCode} - Nhóm ${subject.groupCode}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Grade display
                Column(horizontalAlignment = Alignment.End) {
                    if (subject.finalGrade?.isNotEmpty() == true) {
                        Text(
                            text = subject.finalGrade,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = getGradeColor(subject.finalGrade.toDoubleOrNull())
                        )
                        subject.finalGradeLetter?.let { letter ->
                            Text(
                                text = letter,
                                style = MaterialTheme.typography.bodyMedium,
                                color = getGradeColor(subject.finalGrade?.toDoubleOrNull())
                            )
                        }
                    } else {
                        Text(
                            text = "Chưa có điểm",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Grade details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                subject.examGrade?.let { examGrade ->
                    if (examGrade.isNotEmpty()) {
                        GradeDetail("Thi", examGrade)
                    }
                }
                subject.midtermGrade?.let { midtermGrade ->
                    if (midtermGrade.isNotEmpty()) {
                        GradeDetail("Giữa kỳ", midtermGrade)
                    }
                }
                GradeDetail("Tín chỉ", subject.credits)
                
                // Result badge
                when (subject.result) {
                    1 -> Badge { Text("Đạt") }
                    0 -> Badge(
                        containerColor = MaterialTheme.colorScheme.error
                    ) { Text("Rớt", color = MaterialTheme.colorScheme.onError) }
                    else -> Badge(
                        containerColor = MaterialTheme.colorScheme.outline
                    ) { Text("Chưa có KQ", color = MaterialTheme.colorScheme.onSurface) }
                }
            }
            
            // Special notes
            if (subject.excludeReason.isNotEmpty()) {
                Text(
                    text = subject.excludeReason,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun GradeDetail(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Helper functions for colors
private fun getGpaColor(gpa: Double?): Color {
    return when {
        gpa == null -> Color.Gray
        gpa >= 8.5 -> Color(0xFF4CAF50) // Green
        gpa >= 7.0 -> Color(0xFF2196F3) // Blue  
        gpa >= 5.5 -> Color(0xFFFF9800) // Orange
        else -> Color(0xFFF44336) // Red
    }
}

private fun getGradeColor(grade: Double?): Color {
    return when {
        grade == null -> Color.Gray
        grade >= 8.5 -> Color(0xFF4CAF50) // Green
        grade >= 7.0 -> Color(0xFF2196F3) // Blue
        grade >= 5.5 -> Color(0xFFFF9800) // Orange
        else -> Color(0xFFF44336) // Red
    }
}

private fun getRankColor(rank: String?): Color {
    return when (rank) {
        "Xuất sắc" -> Color(0xFFE91E63) // Pink
        "Giỏi" -> Color(0xFF4CAF50) // Green
        "Khá" -> Color(0xFF2196F3) // Blue
        "Trung bình" -> Color(0xFFFF9800) // Orange
        "Yếu" -> Color(0xFFF44336) // Red
        else -> Color.Gray
    }
}
