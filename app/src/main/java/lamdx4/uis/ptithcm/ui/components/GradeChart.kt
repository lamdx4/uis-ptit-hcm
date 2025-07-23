package lamdx4.uis.ptithcm.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import lamdx4.uis.ptithcm.data.model.SubjectResult
import kotlin.math.abs

@Composable
fun GradeChart(
    subjects: List<SubjectResult>,
    modifier: Modifier = Modifier
) {
    var selectedSubject by remember { mutableStateOf<SubjectResult?>(null) }
    var clickPosition by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current

    Box(modifier = modifier) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Biểu đồ điểm số",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (subjects.isNotEmpty()) {
                    // Scrollable chart container
                    val chartWidth = (subjects.size * 60).dp
                    
                    Column {
                        // Main chart area
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        ) {
                            // Y-axis labels (điểm) - reduced width
                            YAxisLabels(modifier = Modifier.width(24.dp))
                            
                            // Chart area with horizontal scroll
                            Canvas(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .pointerInput(Unit) {
                                        // Handle click events
                                        awaitPointerEventScope {
                                            while (true) {
                                                val event = awaitPointerEvent()
                                                val position = event.changes.first().position
                                                
                                                // Calculate which subject was clicked
                                                val barWidth = size.width / subjects.size.toFloat()
                                                val clickedIndex = (position.x / barWidth).toInt()
                                                
                                                if (clickedIndex in subjects.indices) {
                                                    selectedSubject = subjects[clickedIndex]
                                                    clickPosition = position
                                                }
                                            }
                                        }
                                    }
                            ) {
                                drawGradeChart(subjects, density)
                            }
                        }
                        
                        // X-axis labels (mã môn) - aligned with chart
                        Row {
                            Spacer(modifier = Modifier.width(24.dp)) // Same width as Y-axis
                            XAxisLabels(
                                subjects = subjects,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Không có dữ liệu điểm",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Tooltip popup
        selectedSubject?.let { subject ->
            Popup(
                onDismissRequest = { selectedSubject = null }
            ) {
                SubjectTooltip(
                    subject = subject,
                    onDismiss = { selectedSubject = null }
                )
            }
        }
    }
}

@Composable
private fun YAxisLabels(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        for (score in listOf(10, 8, 6, 4, 2, 0)) {
            Text(
                text = "$score",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun XAxisLabels(
    subjects: List<SubjectResult>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        subjects.forEach { subject ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = subject.ma_doi_tuong.take(8), // Truncate long codes
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGradeChart(
    subjects: List<SubjectResult>,
    density: androidx.compose.ui.unit.Density
) {
    val canvasWidth = size.width
    val canvasHeight = size.height - 20.dp.toPx() // Reduced space for labels
    val barWidth = canvasWidth / subjects.size
    val maxScore = 10f
    
    subjects.forEachIndexed { index, subject ->
        val score = subject.diem_trung_binh2?.toFloat() ?: 0f
        val barHeight = (score / maxScore) * canvasHeight
        val startX = index * barWidth
        
        // Determine bar color based on grade
        val barColor = when {
            score >= 8.5f -> Color(0xFF4CAF50) // Green (A)
            score >= 7.0f -> Color(0xFF2196F3) // Blue (B)  
            score >= 5.5f -> Color(0xFFFFD700) // Gold (C)
            score >= 4.0f -> Color(0xFFFF8C00) // Orange (D)
            else -> Color(0xFFFF5722) // Red (F)
        }
        
        // Draw bar - centered in each section
        if (barHeight > 0) {
            val barPadding = barWidth * 0.15f // Slightly reduce padding
            drawRect(
                color = barColor,
                topLeft = Offset(startX + barPadding, canvasHeight - barHeight),
                size = Size(barWidth - (barPadding * 2), barHeight)
            )
        }
        
        // Draw score text on top of bar
        if (score > 0) {
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    String.format("%.1f", score),
                    startX + barWidth / 2, // Center text in each section
                    canvasHeight - barHeight - 5.dp.toPx(), // Reduced margin
                    android.graphics.Paint().apply {
                        textAlign = android.graphics.Paint.Align.CENTER
                        textSize = with(density) { 11.sp.toPx() }
                        color = android.graphics.Color.BLACK
                        isFakeBoldText = true
                    }
                )
            }
        }
    }
    
    // Draw grid lines for better readability
    val gridColor = Color.Gray.copy(alpha = 0.3f)
    for (score in listOf(2, 4, 6, 8, 10)) {
        val y = canvasHeight - (score / maxScore) * canvasHeight
        drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(canvasWidth, y),
            strokeWidth = 1.dp.toPx()
        )
    }
}

@Composable
private fun SubjectTooltip(
    subject: SubjectResult,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .clickable { onDismiss() }
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = subject.ma_doi_tuong,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = subject.ten_doi_tuong,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Số tín chỉ:",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${subject.so_tin_chi ?: 0}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Điểm số:",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = String.format("%.1f", subject.diem_trung_binh2 ?: 0.0),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        (subject.diem_trung_binh2 ?: 0.0) >= 8.5 -> Color(0xFF4CAF50)
                        (subject.diem_trung_binh2 ?: 0.0) >= 7.0 -> Color(0xFF2196F3)
                        (subject.diem_trung_binh2 ?: 0.0) >= 5.5 -> Color(0xFFFFD700)
                        (subject.diem_trung_binh2 ?: 0.0) >= 4.0 -> Color(0xFFFF8C00)
                        else -> Color(0xFFFF5722)
                    }
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Điểm chữ:",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = subject.diem_chu ?: "N/A",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Kết quả:",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = subject.ket_qua ?: "N/A",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (subject.ket_qua == "Đạt") Color(0xFF4CAF50) else Color(0xFFFF5722)
                )
            }
        }
    }
}
