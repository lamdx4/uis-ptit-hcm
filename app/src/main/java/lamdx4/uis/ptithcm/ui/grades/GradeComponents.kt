package lamdx4.uis.ptithcm.ui.grades

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun SubjectGradeCard(subject: SubjectGrade) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
