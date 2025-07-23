package lamdx4.uis.ptithcm.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import lamdx4.uis.ptithcm.data.model.DaySchedule
import lamdx4.uis.ptithcm.data.model.ScheduleItem
import lamdx4.uis.ptithcm.data.model.WeeklySchedule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekSelector(
    weeks: List<WeeklySchedule>,
    selectedWeek: WeeklySchedule?,
    onWeekSelected: (WeeklySchedule) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedWeek?.weekInfo ?: "Chọn tuần học",
            onValueChange = {},
            readOnly = true,
            label = { Text("Tuần học") },
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
            weeks.forEach { week ->
                DropdownMenuItem(
                    text = { 
                        Column {
                            Text(
                                text = week.weekInfo ?: "Tuần học",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${week.startDate} - ${week.endDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        onWeekSelected(week)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun WeeklyScheduleView(
    daySchedules: List<DaySchedule>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(daySchedules) { daySchedule ->
            DayScheduleCard(daySchedule = daySchedule)
        }
    }
}

@Composable
fun DayScheduleCard(
    daySchedule: DaySchedule,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (daySchedule.scheduleItems.isEmpty()) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Day header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = daySchedule.dayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = daySchedule.date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (daySchedule.scheduleItems.isEmpty()) {
                Text(
                    text = "Không có lịch học",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                // Schedule items
                daySchedule.scheduleItems.forEach { item ->
                    ScheduleItemCard(
                        scheduleItem = item,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun ScheduleItemCard(
    scheduleItem: ScheduleItem,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val backgroundColor = getSubjectColor(scheduleItem.subjectCode)
    
    Card(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor.copy(alpha = 0.1f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            width = 1.dp,
            brush = androidx.compose.ui.graphics.SolidColor(backgroundColor)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Subject name and time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = scheduleItem.subjectName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = backgroundColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = scheduleItem.subjectCode,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Time badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = backgroundColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = getPeriodTimeText(scheduleItem.startPeriod, scheduleItem.numberOfPeriods),
                        style = MaterialTheme.typography.labelSmall,
                        color = backgroundColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            
            Divider(color = backgroundColor.copy(alpha = 0.3f))
            
            // Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Room
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = scheduleItem.roomCode.takeIf { it.isNotEmpty() } ?: "---",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Teacher
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = scheduleItem.teacherName.takeIf { it.isNotEmpty() } ?: "---",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Additional info if available
            if (scheduleItem.groupCode.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Nhóm ${scheduleItem.groupCode}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// Helper functions
private fun getSubjectColor(subjectCode: String): Color {
    val colors = listOf(
        Color(0xFF1976D2), // Blue
        Color(0xFF388E3C), // Green  
        Color(0xFFF57C00), // Orange
        Color(0xFF7B1FA2), // Purple
        Color(0xFFD32F2F), // Red
        Color(0xFF00796B), // Teal
        Color(0xFF5D4037), // Brown
        Color(0xFF455A64), // Blue Grey
    )
    
    return colors[subjectCode.hashCode().rem(colors.size).let { if (it < 0) it + colors.size else it }]
}

private fun getPeriodTimeText(startPeriod: Int?, numberOfPeriods: Int?): String {
    if (startPeriod == null || numberOfPeriods == null) return "---"
    
    val startTime = getPeriodStartTime(startPeriod)
    val endTime = getPeriodStartTime(startPeriod + numberOfPeriods)
    
    return "Tiết $startPeriod-${startPeriod + numberOfPeriods - 1}\n$startTime-$endTime"
}

private fun getPeriodStartTime(period: Int): String {
    return when (period) {
        1 -> "07:00"
        2 -> "07:50"
        3 -> "08:50"
        4 -> "09:40"
        5 -> "10:40"
        6 -> "11:30"
        7 -> "12:30"
        8 -> "13:20"
        9 -> "14:20"
        10 -> "15:10"
        11 -> "16:10"
        12 -> "17:00"
        13 -> "18:00"
        14 -> "18:50"
        15 -> "19:40"
        else -> "---"
    }
}
