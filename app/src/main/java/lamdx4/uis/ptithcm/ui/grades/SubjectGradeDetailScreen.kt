package lamdx4.uis.ptithcm.ui.grades

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import lamdx4.uis.ptithcm.data.model.SubjectGrade
import lamdx4.uis.ptithcm.data.model.ComponentGrade

@Composable
fun SubjectGradeDetailDialog(
    subject: SubjectGrade,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Subject header
            SubjectDetailHeader(subject)
            
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
            
            // Grade breakdown
            GradeBreakdown(subject)
            
            // Component grades (if available)
            if (subject.componentGrades.isNotEmpty()) {
                ComponentGradesSection(subject.componentGrades)
            }
            
            // Close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Đóng")
                }
            }
        }
    }
}

@Composable
private fun SubjectDetailHeader(subject: SubjectGrade) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = subject.subjectName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Text(
            text = "${subject.subjectCode} - Nhóm ${subject.groupCode}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoChip(
                label = "Tín chỉ",
                value = subject.credits,
                icon = Icons.Default.AssignmentTurnedIn
            )
            InfoChip(
                label = "Môn ngành",
                value = if (subject.isMajorSubject) "Có" else "Không",
                icon = Icons.Default.Info
            )
        }
    }
}

@Composable
private fun GradeBreakdown(subject: SubjectGrade) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Chi tiết điểm số",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Midterm grade
                if (!subject.midtermGrade.isNullOrEmpty()) {
                    GradeItem(
                        label = "Giữa kỳ",
                        value = subject.midtermGrade,
                        icon = Icons.Default.Quiz,
                        color = getGradeColor(subject.midtermGrade.toDoubleOrNull())
                    )
                }
                
                // Exam grade
                if (!subject.examGrade.isNullOrEmpty()) {
                    GradeItem(
                        label = "Cuối kỳ",
                        value = subject.examGrade,
                        icon = Icons.Default.Grade,
                        color = getGradeColor(subject.examGrade.toDoubleOrNull())
                    )
                }
                
                // Final grade
                if (!subject.finalGrade.isNullOrEmpty()) {
                    GradeItem(
                        label = "Tổng kết",
                        value = subject.finalGrade,
                        icon = Icons.Default.School,
                        color = getGradeColor(subject.finalGrade.toDoubleOrNull()),
                        isHighlighted = true
                    )
                }
            }
            
            // Grade scale and letter
            if (!subject.finalGrade4.isNullOrEmpty() || !subject.finalGradeLetter.isNullOrEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    subject.finalGrade4?.let { grade4 ->
                        if (grade4.isNotEmpty()) {
                            ScaleGradeItem(
                                label = "Thang điểm 4",
                                value = grade4,
                                color = getGradeColor(grade4.toDoubleOrNull())
                            )
                        }
                    }
                    
                    subject.finalGradeLetter?.let { gradeLetter ->
                        if (gradeLetter.isNotEmpty()) {
                            ScaleGradeItem(
                                label = "Điểm chữ",
                                value = gradeLetter,
                                color = getLetterGradeColor(gradeLetter)
                            )
                        }
                    }
                }
            }
            
            // Result
            val resultColor = when (subject.result) {
                1 -> Color(0xFF4CAF50) // Green for pass
                0 -> Color(0xFFF44336) // Red for fail
                else -> MaterialTheme.colorScheme.outline // Gray for no result
            }
            
            val resultText = when (subject.result) {
                1 -> "Đạt"
                0 -> "Không đạt"
                else -> "Chưa có kết quả"
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        resultColor.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Kết quả: $resultText",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = resultColor
                )
            }
        }
    }
}

@Composable
private fun ComponentGradesSection(componentGrades: List<ComponentGrade>) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Điểm thành phần (${componentGrades.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Thu gọn" else "Mở rộng",
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    componentGrades.forEach { component ->
                        ComponentGradeItem(component)
                    }
                }
            }
        }
    }
}

@Composable
private fun ComponentGradeItem(componentGrade: ComponentGrade) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = componentGrade.componentName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Ký hiệu: ${componentGrade.symbol}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = componentGrade.grade.ifEmpty { "Chưa có" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (componentGrade.grade.isNotEmpty()) {
                        getGradeColor(componentGrade.grade.toDoubleOrNull())
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                Text(
                    text = "Trọng số: ${componentGrade.weight}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun InfoChip(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun GradeItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isHighlighted: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(if (isHighlighted) 28.dp else 24.dp)
        )
        Text(
            text = value,
            style = if (isHighlighted) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ScaleGradeItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// Helper functions for colors
private fun getGradeColor(grade: Double?): Color {
    return when {
        grade == null -> Color.Gray
        grade >= 8.5 -> Color(0xFF4CAF50) // Green (A)
        grade >= 7.0 -> Color(0xFF2196F3) // Blue (B)
        grade >= 5.5 -> Color(0xFFFF9800) // Orange (C)
        grade >= 4.0 -> Color(0xFFFF8C00) // Dark Orange (D)
        else -> Color(0xFFF44336) // Red (F)
    }
}

private fun getLetterGradeColor(letterGrade: String): Color {
    return when (letterGrade.uppercase()) {
        "A", "A+" -> Color(0xFF4CAF50) // Green
        "B", "B+" -> Color(0xFF2196F3) // Blue
        "C", "C+" -> Color(0xFFFF9800) // Orange
        "D", "D+" -> Color(0xFFFF8C00) // Dark Orange
        "F" -> Color(0xFFF44336) // Red
        else -> Color.Gray
    }
}
