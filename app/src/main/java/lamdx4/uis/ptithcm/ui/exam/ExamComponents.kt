package lamdx4.uis.ptithcm.ui.exam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import lamdx4.uis.ptithcm.data.model.AlarmEntity
import lamdx4.uis.ptithcm.data.model.Exam
import lamdx4.uis.ptithcm.data.model.ExamItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun DatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    DisposableEffect(Unit) {
        val dialog = android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val day = String.format(Locale.US, "%02d", dayOfMonth)   // luôn 2 chữ số
                val mon = String.format(Locale.US, "%02d", month + 1)   // month tính từ 0 → cần +1
                onDateSelected("$day/$mon/$year")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.setOnDismissListener { onDismiss() }
        dialog.show()

        onDispose {
            dialog.dismiss()
        }
    }

}

@Composable
fun PersonalExamItem(
    exam: Exam,                     // model của lịch thi
    alarms: List<AlarmEntity>,      // lấy từ viewModel.alarms.collectAsState()
    onAddAlarm: (AlarmEntity) -> Unit,
    onDeleteAlarm: (AlarmEntity) -> Unit
) {
    val examMillis = remember(exam) {
        // Parse exam.examDate + exam.startTime thành timestamp millis
        parseExamDateTime(exam.examDate, exam.startTime)
    }

    // Tìm xem alarm có tồn tại chưa
    val existingAlarm = alarms.firstOrNull { it.time == examMillis }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${exam.subjectName} - ${exam.subjectCode}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "Ngày thi: ${exam.examDate}")
                    Text(text = "Giờ bắt đầu: ${exam.startTime}")
                    Text(text = "Phòng: ${exam.examLocation}")
                    Text(text = "Hình thức: ${exam.examFormat}")
                    Text(text = "Thời gian thi: ${exam.durationMinutes} phút")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = "Alarm icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = existingAlarm != null,
                        onCheckedChange = { checked ->
                            if (checked && existingAlarm == null) {
                                onAddAlarm(
                                    AlarmEntity(
                                        time = examMillis,
                                        label = "${exam.subjectName} (${exam.subjectCode})",
                                        toneUri = null,
                                        vibrate = true
                                    )
                                )
                            } else if (!checked && existingAlarm != null) {
                                onDeleteAlarm(existingAlarm)
                            }
                        }
                    )
                }

            }
        }
    }
}

@Composable
fun SubtypeExamItem(
    exam: ExamItem,                     // model của lịch thi
    alarms: List<AlarmEntity>,      // lấy từ viewModel.alarms.collectAsState()
    onAddAlarm: (AlarmEntity) -> Unit,
    onDeleteAlarm: (AlarmEntity) -> Unit
) {
    val examMillis = remember(exam) {
        // Parse exam.examDate + exam.startTime thành timestamp millis
        parseExamDateTime(exam.examDate, exam.startTime)
    }

    // Tìm xem alarm có tồn tại chưa
    val existingAlarm = alarms.firstOrNull { it.time == examMillis }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${exam.subjectName} - ${exam.subjectCode}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "Ngày thi: ${exam.examDate}")
                    Text(text = "Giờ bắt đầu: ${exam.startTime}")
                    Text(text = "Phòng: ${exam.examLocation}")
                    Text(text = "Hình thức: ${exam.examFormat}")
                    Text(text = "Thời gian thi: ${exam.durationMinutes} phút")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = "Alarm icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = existingAlarm != null,
                        onCheckedChange = { checked ->
                            if (checked && existingAlarm == null) {
                                onAddAlarm(
                                    AlarmEntity(
                                        time = examMillis,
                                        label = "${exam.subjectName} (${exam.subjectCode})",
                                        toneUri = null,
                                        vibrate = true
                                    )
                                )
                            } else if (!checked && existingAlarm != null) {
                                onDeleteAlarm(existingAlarm)
                            }
                        }
                    )
                }
            }
        }
    }
}

fun parseExamDateTime(date: String, time: String): Long {
    // date: "dd/MM/yyyy", time: "HH:mm"
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val dateTime = formatter.parse("$date $time")
    return dateTime?.time ?: 0L
}
