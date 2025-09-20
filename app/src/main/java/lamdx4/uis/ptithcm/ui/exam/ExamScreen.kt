package lamdx4.uis.ptithcm.ui.exam

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import lamdx4.uis.ptithcm.common.activityViewModel
import lamdx4.uis.ptithcm.ui.AppViewModel

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
    val alarms by viewModel.alarms.collectAsState()

    val refreshCoordinator = appViewModel.refreshCoordinator

    // TRYING to save states for dropdown to match with data below
    val selectedSemester by viewModel.selectedSemester.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val selectedSubType by viewModel.selectedSubType.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        refreshCoordinator.refreshEvent.collect { route ->
            if (route == "exam") {
                viewModel.refreshPersonalExams(
                    examSemesterResponse?.data?.semesters
                        ?.first()?.semesterCode ?: 20243
                )
                viewModel.refreshExamTypes()
                viewModel.refreshExamSubTypes(
                    examSemesterResponse?.data?.semesters
                        ?.first()?.semesterCode ?: 20243,
                    examTypeResponse?.data?.scheduleObjects[1]?.objectType ?: 3
                )
                viewModel.refreshExamSemesters()
                viewModel.loadAlarms()
            }
        }
    }

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
                // Semester dropdown
                DropdownSelector(
                    label = "Học Kỳ",
                    options = examSemesterResponse?.data?.semesters?.map { it.semesterName }
                        ?: emptyList(),
                    selectedOption = examSemesterResponse?.data?.semesters
                        ?.firstOrNull { it.semesterCode == selectedSemester }?.semesterName ?: "",
                    onOptionSelected = { option ->
                        val semesterId = examSemesterResponse?.data?.semesters
                            ?.firstOrNull { it.semesterName == option }?.semesterCode
                        if (semesterId != null) {
                            viewModel.setSelectedSemester(semesterId)
                            if (selectedType == 1) {
                                viewModel.refreshPersonalExams(semesterId)
                            } else {
                                viewModel.refreshExamSubTypes(
                                    semesterId, selectedType
                                )
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Type dropdown
                DropdownSelector(
                    label = "Loại lịch thi",
                    options = examTypeResponse?.data?.scheduleObjects?.map { it.objectName }
                        ?: emptyList(),
                    selectedOption = examTypeResponse?.data?.scheduleObjects
                        ?.firstOrNull { it.objectType == selectedType }?.objectName ?: "",
                    onOptionSelected = { option ->
                        val typeId = examTypeResponse?.data?.scheduleObjects
                            ?.firstOrNull { it.objectName == option }?.objectType
                        if (typeId != null) {
                            viewModel.setSelectedType(typeId)
                            if (typeId != 5) {
                                viewModel.refreshExamSubTypes(selectedSemester, typeId)
                            }
                            showDatePicker = typeId == 5
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Nếu type = 5 => hiển thị chọn ngày
                if (selectedType == 5) {
                    OutlinedTextField(
                        value = selectedDate.ifEmpty { "Chọn ngày" },
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth(),
                        label = { Text("Chọn ngày") },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange, // icon calendar
                                    contentDescription = "Chọn lại ngày"
                                )
                            }
                        }
                    )

                    if (showDatePicker) {
                        DatePickerDialog(
                            onDateSelected = { date ->
                                viewModel.setSelectedDate(date)
                                showDatePicker = false
                                viewModel.refreshSubTypeExams(
                                    semester = selectedSemester,
                                    examType = selectedType,
                                    subType = "",
                                    examDate = selectedDate,
                                )
                            },
                            onDismiss = { showDatePicker = false }
                        )
                    }
                } else if (selectedType != 1) {
                    // Chỉ hiện subtype khi type != 5
                    DropdownSelector(
                        label = "Chọn SubType",
                        options = examSubTypeResponse?.data?.dataItems?.map { it.dataName }
                            ?: emptyList(),
                        selectedOption = selectedSubType,
                        onOptionSelected = { option ->
                            val subTypeId =
                                examSubTypeResponse?.data?.dataItems?.firstOrNull { it.dataName == option }?.dataId
                            viewModel.setSelectedSubType(option)
                            viewModel.refreshSubTypeExams(
                                semester = selectedSemester,
                                examType = selectedType,
                                subType = subTypeId ?: "",
                                examDate = ""
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (selectedType == 1) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(personalExamResponse?.data?.examSchedules ?: emptyList()) { exam ->
                            PersonalExamItem(
                                exam = exam,
                                alarms = alarms,
                                onAddAlarm = viewModel::addAlarm,
                                onDeleteAlarm = viewModel::deleteAlarm
                            )
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(
                            subTypeExamResponse?.studentExamSchedule?.data?.examList ?: emptyList()
                        ) { exam ->
                            SubtypeExamItem(
                                exam = exam,
                                alarms = alarms,
                                onAddAlarm = viewModel::addAlarm,
                                onDeleteAlarm = viewModel::deleteAlarm
                            )
                        }
                    }
                }
            }
        }
    }
}
