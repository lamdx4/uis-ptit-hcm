package lamdx4.uis.ptithcm.ui.fee

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.navigation.NavController
import lamdx4.uis.ptithcm.common.activityViewModel
import lamdx4.uis.ptithcm.data.model.TuitionFeeSemester
import lamdx4.uis.ptithcm.ui.AppViewModel
import lamdx4.uis.ptithcm.ui.theme.PTITColors
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeeScreen(
    modifier: Modifier = Modifier,
    viewModel: FeeViewModel = hiltViewModel(),
    appViewModel: AppViewModel = activityViewModel<AppViewModel>(),
    navController: NavController
) {
    val totalTuitionFeeResponse by viewModel.totalTuitionFee.collectAsState()
    val tuitionFeeSemesterResponse by viewModel.tuitionFeeSemester.collectAsState()
    val detailTuitionFeeResponse by viewModel.detailTuitionFee.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val refreshCoordinator = appViewModel.refreshCoordinator

    var selectedSemester by remember { mutableStateOf<TuitionFeeSemester?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        refreshCoordinator.refreshEvent.collect { route ->
            if (route == "fee") {
                viewModel.refreshTotalTuitionFee()
                viewModel.refreshTuitionFeeSemester()
                selectedSemester?.let {
                    viewModel.refreshDetailTuitionFee(it.semesterCode)
                }
            }
        }
    }

    LaunchedEffect(selectedSemester) {
        selectedSemester?.let {
            viewModel.refreshDetailTuitionFee(it.semesterCode)
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
                text = errorMessage ?: "Đã có lỗi xảy ra",
                color = PTITColors.redDefault,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // Dropdown và buttons
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Dropdown
                        ExposedDropdownMenuBox(
                            expanded = isDropdownExpanded,
                            onExpandedChange = { isDropdownExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedSemester?.semesterName
                                    ?: "Tổng hợp học phí tất cả học kỳ",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                                },
                                modifier = Modifier
                                    .menuAnchor(
                                        type = MenuAnchorType.PrimaryNotEditable,
                                        enabled = true
                                    )
                                    .fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PTITColors.info,
                                    unfocusedBorderColor = PTITColors.neutralMuted
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Tổng hợp học phí tất cả học kỳ") },
                                    onClick = {
                                        selectedSemester = null
                                        isDropdownExpanded = false
                                    }
                                )
                                tuitionFeeSemesterResponse?.data?.semesterList?.forEach { semester ->
                                    DropdownMenuItem(
                                        text = { Text(semester.semesterName) },
                                        onClick = {
                                            selectedSemester = semester
                                            isDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

//                        Spacer(modifier = Modifier.width(8.dp))

                        // Print button
//                        IconButton(
//                            onClick = {},
//                            colors = IconButtonDefaults.iconButtonColors(
//                                containerColor = PTITColors.info
//                            )
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Print,
//                                contentDescription = "In",
//                                tint = Color.White
//                            )
//                        }
//
                        Spacer(modifier = Modifier.width(8.dp))

                        // Export Excel button
                        IconButton(
                            onClick = {
                                val targetUrl = "https://uis.ptithcm.edu.vn/#/hocphi"

                                // cần Encode URL vì nó có ký tự đặc biệt (#, /)
                                val encodedUrl = java.net.URLEncoder.encode(
                                    targetUrl,
                                    java.nio.charset.StandardCharsets.UTF_8.toString()
                                )

                                // Điều hướng
                                navController.navigate("export_webview?url=$encodedUrl")
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = "Mở web tải file",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Content based on selection
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    if (selectedSemester == null) {
                        // Show total tuition fee table
                        TotalTuitionFeeTable(
                            data = totalTuitionFeeResponse,
                            modifier = Modifier
                        )
                    } else {
                        // Show detail tuition fee table
                        DetailTuitionFeeTable(
                            data = detailTuitionFeeResponse,
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}

fun formatCurrency(value: String): String {
    val number = value.toLongOrNull() ?: return "0"
    return String.format(Locale.US, "%,d", number).replace(",", ".")
}
