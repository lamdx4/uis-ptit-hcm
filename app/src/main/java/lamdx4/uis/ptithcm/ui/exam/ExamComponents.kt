package lamdx4.uis.ptithcm.ui.exam

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import lamdx4.uis.ptithcm.data.model.AlarmEntity
import lamdx4.uis.ptithcm.data.model.Exam
import lamdx4.uis.ptithcm.data.model.ExamItem
import lamdx4.uis.ptithcm.service.AlarmScheduler
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
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
    exam: Exam,
    alarms: List<AlarmEntity>,
    onAddAlarm: (AlarmEntity) -> Unit,
    onDeleteAlarm: (AlarmEntity) -> Unit
) {
    val context = LocalContext.current

    val existingAlarm = alarms.firstOrNull { it.label == "Nhắc nhở: ${exam.subjectName}" }

    var showDateTimePicker by remember { mutableStateOf(false) }

    // Format thời gian đẹp để hiển thị
    fun formatAlarmTime(millis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(millis))
    }

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

                    // Nếu có báo thức thì hiển thị thêm thời gian đã đặt
                    if (existingAlarm != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "⏰ Báo thức: ${formatAlarmTime(existingAlarm.time)}",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
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
                                context.ensureNotificationAndAlarmPermission()
                                showDateTimePicker = true
                            } else if (!checked && existingAlarm != null) {
                                onDeleteAlarm(existingAlarm)
                                AlarmScheduler().cancelAlarm(
                                    context = context,
                                    requestCode = existingAlarm.time.toInt()
                                )
                            }
                        }
                    )
                }
            }

            if (showDateTimePicker) {
                DateTimePickerDialog(
                    onDismiss = { showDateTimePicker = false },
                    onConfirm = { dateStr, timeStr, millis ->
                        showDateTimePicker = false
                        if (millis <= System.currentTimeMillis()) {
                            Toast.makeText(context, "Thời gian đã qua!", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            val alarm = AlarmEntity(
                                time = millis,
                                label = "Nhắc nhở: ${exam.subjectName}",
                                toneUri = null,
                                vibrate = true
                            )
                            onAddAlarm(alarm)
                            AlarmScheduler().scheduleExactAlarm(
                                context = context,
                                requestCode = millis.toInt(),
                                triggerAtMillis = millis,
                                label = alarm.label
                            )
                        }
                    }
                )
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
    val context = LocalContext.current

    val existingAlarm = alarms.firstOrNull { it.label == "Nhắc nhở: ${exam.subjectName}" }

    var showDateTimePicker by remember { mutableStateOf(false) }

    // Format thời gian đẹp để hiển thị
    fun formatAlarmTime(millis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(millis))
    }

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

                    // Nếu có báo thức thì hiển thị thêm thời gian đã đặt
                    if (existingAlarm != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "⏰ Báo thức: ${formatAlarmTime(existingAlarm.time)}",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
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
                                context.ensureNotificationAndAlarmPermission()
                                showDateTimePicker = true
                            } else if (!checked && existingAlarm != null) {
                                onDeleteAlarm(existingAlarm)
                                AlarmScheduler().cancelAlarm(
                                    context = context,
                                    requestCode = existingAlarm.time.toInt()
                                )
                            }
                        }
                    )
                }
            }

            if (showDateTimePicker) {
                DateTimePickerDialog(
                    onDismiss = { showDateTimePicker = false },
                    onConfirm = { dateStr, timeStr, millis ->
                        showDateTimePicker = false
                        if (millis <= System.currentTimeMillis()) {
                            Toast.makeText(context, "Thời gian đã qua!", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            val alarm = AlarmEntity(
                                time = millis,
                                label = "Nhắc nhở: ${exam.subjectName}",
                                toneUri = null,
                                vibrate = true
                            )
                            onAddAlarm(alarm)
                            AlarmScheduler().scheduleExactAlarm(
                                context = context,
                                requestCode = millis.toInt(),
                                triggerAtMillis = millis,
                                label = alarm.label
                            )
                        }
                    }
                )
            }
        }
    }
}

fun Context.ensureNotificationAndAlarmPermission() {
    val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // 1. Android 13+ → check POST_NOTIFICATIONS
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (this is Activity) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            } else {
                // Optionally, handle the case where context is not an Activity
                Toast.makeText(
                    this,
                    "Cannot request permission: context is not an Activity.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // 2. Check app notifications có bị block không
    if (!nm.areNotificationsEnabled()) {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    // 3. Android 14+ → check quyền full-screen intent cho alarm
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        val granted = nm.canUseFullScreenIntent()
        if (!granted) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT).apply {
                data = "package:$packageName".toUri()
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Long) -> Unit
) {
    val calendar = remember { Calendar.getInstance() }

    var pickedDate by remember {
        mutableLongStateOf(calendar.timeInMillis)
    }
    var pickedTime by remember {
        mutableLongStateOf(calendar.timeInMillis)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                // Parse thành millis
                val finalCal = Calendar.getInstance().apply {
                    timeInMillis = pickedDate
                    val timeCal = Calendar.getInstance().apply { timeInMillis = pickedTime }
                    set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
                    set(Calendar.SECOND, 0)
                }

                val dateStr =
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(finalCal.time)
                val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(finalCal.time)
                onConfirm(dateStr, timeStr, finalCal.timeInMillis)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        },
        title = { Text("Chọn ngày & giờ") },
        text = {
            Column {
                AndroidView(
                    factory = { context ->
                        val picker = DatePicker(context)
                        picker.init(
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ) { _, year, month, day ->
                            val cal = Calendar.getInstance()
                            cal.set(year, month, day)
                            pickedDate = cal.timeInMillis
                        }
                        picker   // 👈 return DatePicker
                    },
                    modifier = Modifier.fillMaxWidth()
                )


                Spacer(Modifier.height(16.dp))

                // Time Picker
                AndroidView(
                    factory = { context ->
                        val picker = TimePicker(context)
                        picker.setIs24HourView(true)
                        picker.setOnTimeChangedListener { _, hour, minute ->
                            val cal = Calendar.getInstance()
                            cal.set(Calendar.HOUR_OF_DAY, hour)
                            cal.set(Calendar.MINUTE, minute)
                            pickedTime = cal.timeInMillis
                        }
                        picker   // 👈 return TimePicker
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
