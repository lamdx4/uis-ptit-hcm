package lamdx4.uis.ptithcm.ui.exam

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import lamdx4.uis.ptithcm.common.activityViewModel
import lamdx4.uis.ptithcm.ui.AppViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    modifier: Modifier = Modifier,
    viewModel: ExamViewModel = hiltViewModel(),
    appViewModel: AppViewModel = activityViewModel<AppViewModel>()
) {
    val examSemesterResponse by viewModel.examSemesterState.collectAsState()
    val examTypeResponse by viewModel.examTypeState.collectAsState()
    val personalExamResponse by viewModel.personalExamState.collectAsState()
    val examSubTypeResponse by viewModel.examSubTypeState.collectAsState()
    val subTypeExamResponse by viewModel.subTypeExamState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val refreshCoordinator = appViewModel.refreshCoordinator
//    val isRefreshing by viewModel.isRefreshing.collectAsState() // not in use

    LaunchedEffect(Unit) {
        refreshCoordinator.refreshEvent.collect { route ->
            if (route == "exam") {
                viewModel.refreshPersonalExams(20243)
                viewModel.refreshExamTypes()
//                viewModel.refreshExamSubTypes(20243, 3)
//                viewModel.refreshExamSemesters()
//                viewModel.refreshSubTypeExams(
//                    20243, 3, "-7832454252451327385", ""
//                )
            }
        }
    }

    var selectedSemester by remember { mutableStateOf<Int?>(null) }
    var selectedType by remember { mutableStateOf<Int?>(null) }
    var selectedSubType by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (!errorMessage.isNullOrEmpty()) {
            Text(
                text = errorMessage!!,
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                DropdownSelector(
                    label = "Chọn kỳ học",
                    options = examSemesterResponse?.data?.semesters?.map { it.semesterName } ?: emptyList(),
                    selectedOption = examSemesterResponse?.data?.semesters
                        ?.firstOrNull { it.semesterCode == selectedSemester }?.semesterName ?: "",
                    onOptionSelected = { option ->
                        val semesterId = examSemesterResponse?.data?.semesters
                            ?.firstOrNull { it.semesterName == option }?.semesterCode
                        if (semesterId != null) {
                            selectedSemester = semesterId
                            viewModel.refreshExamSubTypes(
                                semesterId,
                                selectedType ?: 0
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                DropdownSelector(
                    label = "Chọn loại lịch thi",
                    options = examTypeResponse?.data?.scheduleObjects?.map { it.objectName } ?: emptyList(),
                    selectedOption = examTypeResponse?.data?.scheduleObjects
                        ?.firstOrNull { it.objectType == selectedType }?.objectName ?: "",
                    onOptionSelected = { option ->
                        val typeId = examTypeResponse?.data?.scheduleObjects
                            ?.firstOrNull { it.objectName == option }?.objectType
                        if (typeId != null) {
                            selectedType = typeId
                            showDatePicker = typeId == 5
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (selectedType != null && examTypeResponse?.data?.scheduleObjects?.firstOrNull { it.objectType == selectedType }?.objectType == 5) {
                    OutlinedTextField(
                        value = selectedDate.ifEmpty { "Chọn ngày" },
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        label = { Text("Chọn ngày") }
                    )

                    if (showDatePicker) {
                        DatePickerDialog(
                            onDateSelected = { date ->
                                selectedDate = date
                                showDatePicker = false
                            },
                            onDismiss = { showDatePicker = false }
                        )
                    }
                } else {
                    DropdownSelector(
                        label = "Chọn SubType",
                        options = examSubTypeResponse?.data?.dataItems?.map { it.dataName }
                            ?: emptyList(),
                        selectedOption = selectedSubType,
                        onOptionSelected = { option ->
                            selectedSubType = option
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(personalExamResponse?.data?.examSchedules ?: emptyList()) { exam ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = exam.subjectName,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(text = "Ngày thi: ${exam.examDate}")
                                Text(text = "Giờ bắt đầu: ${exam.startTime}")
                                Text(text = "Phòng: ${exam.examLocation}")
                                Text(text = "Hình thức: ${exam.examFormat}")
                            }
                        }
                    }
                }
            }
        }
    }
}

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
                .menuAnchor()
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
                onDateSelected("$dayOfMonth/${month + 1}/$year")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.setOnDismissListener { onDismiss() }
        dialog.show()

        onDispose {
            dialog.dismiss() // Dọn dẹp khi Composable bị remove
        }
    }
}
