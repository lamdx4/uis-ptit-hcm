package lamdx4.uis.ptithcm.ui.grades

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import lamdx4.uis.ptithcm.data.model.ComponentGrade
import lamdx4.uis.ptithcm.data.model.SubjectGrade

@Composable
fun SubjectDetailDialog(
    subject: SubjectGrade,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Grade,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Chi tiết điểm",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng"
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Subject info
                    item {
                        SubjectInfoCard(subject)
                    }
                    
                    // Grade summary
                    item {
                        GradeSummaryCard(subject)
                    }
                    
                    // Component grades
                    if (subject.componentGrades.isNotEmpty()) {
                        item {
                            Text(
                                text = "Điểm thành phần",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        items(subject.componentGrades) { component ->
                            ComponentGradeCard(component)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectInfoCard(subject: SubjectGrade) {
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
            Text(
                text = subject.subjectName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Text(
                text = "Mã môn: ${subject.subjectCode}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Text(
                text = "Nhóm: ${subject.groupCode}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Text(
                text = "Số tín chỉ: ${subject.credits}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun GradeSummaryCard(subject: SubjectGrade) {
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Kết quả học tập",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Exam grade
                subject.examGrade?.let { examGrade ->
                    if (examGrade.isNotEmpty()) {
                        GradeDetailItem("Điểm thi", examGrade)
                    }
                }
                
                // Midterm grade
                subject.midtermGrade?.let { midtermGrade ->
                    if (midtermGrade.isNotEmpty()) {
                        GradeDetailItem("Điểm giữa kỳ", midtermGrade)
                    }
                }
                
                // Final grade
                subject.finalGrade?.let { finalGrade ->
                    if (finalGrade.isNotEmpty()) {
                        GradeDetailItem("Điểm cuối kỳ", finalGrade)
                    }
                }
            }
            
            // Final result
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (subject.finalGrade?.isNotEmpty() == true) {
                    Column {
                        Text(
                            text = "Điểm tổng kết",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                    }
                } else {
                    Text(
                        text = "Chưa có điểm tổng kết",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Result badge
                when (subject.result) {
                    1 -> Badge(
                        containerColor = MaterialTheme.colorScheme.primary
                    ) { 
                        Text("ĐẠT", color = MaterialTheme.colorScheme.onPrimary) 
                    }
                    0 -> Badge(
                        containerColor = MaterialTheme.colorScheme.error
                    ) { 
                        Text("KHÔNG ĐẠT", color = MaterialTheme.colorScheme.onError) 
                    }
                    else -> Badge(
                        containerColor = MaterialTheme.colorScheme.outline
                    ) { 
                        Text("CHƯA CÓ KQ", color = MaterialTheme.colorScheme.onSurface) 
                    }
                }
            }
            
            // Exclude reason if any
            if (subject.excludeReason.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = "Ghi chú: ${subject.excludeReason}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ComponentGradeCard(component: ComponentGrade) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = component.componentName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                if (component.symbol.isNotEmpty()) {
                    Text(
                        text = "Ký hiệu: ${component.symbol}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = component.grade,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = getGradeColor(component.grade.toDoubleOrNull())
                )
                Text(
                    text = "Trọng số: ${component.weight}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun GradeDetailItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = getGradeColor(value.toDoubleOrNull())
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Helper function for grade colors
private fun getGradeColor(grade: Double?): Color {
    return when {
        grade == null -> Color.Gray
        grade >= 8.5 -> Color(0xFF4CAF50) // Green
        grade >= 7.0 -> Color(0xFF2196F3) // Blue
        grade >= 5.5 -> Color(0xFFFF9800) // Orange
        else -> Color(0xFFF44336) // Red
    }
}
