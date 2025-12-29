package lamdx4.uis.ptithcm.ui.fee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import lamdx4.uis.ptithcm.common.activityViewModel
import lamdx4.uis.ptithcm.data.model.DetailTuitionFeeResponse
import lamdx4.uis.ptithcm.data.model.PaidItem
import lamdx4.uis.ptithcm.data.model.PayableItem
import lamdx4.uis.ptithcm.data.model.TotalTuitionFeeResponse
import lamdx4.uis.ptithcm.data.model.TuitionFeePerSemester
import lamdx4.uis.ptithcm.data.model.TuitionFeeSemester
import lamdx4.uis.ptithcm.ui.AppViewModel
import lamdx4.uis.ptithcm.ui.theme.PTITColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeeScreen(
    modifier: Modifier = Modifier,
    viewModel: FeeViewModel = hiltViewModel(),
    appViewModel: AppViewModel = activityViewModel<AppViewModel>()
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
                        modifier = Modifier.padding(16.dp),
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
                                    .menuAnchor()
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

                        Spacer(modifier = Modifier.width(8.dp))

                        // Print button
                        IconButton(
                            onClick = { /* TODO */ },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = PTITColors.info
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Print,
                                contentDescription = "In",
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Export Excel button
                        IconButton(
                            onClick = { /* TODO */ },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = PTITColors.success
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileDownload,
                                contentDescription = "Xuất Excel",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Content based on selection
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

@Composable
fun TotalTuitionFeeTable(
    data: TotalTuitionFeeResponse?,
    modifier: Modifier = Modifier
) {
    if (data == null) return

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 1.dp
    ) {
        Column {
            // Table header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PTITColors.neutralMuted)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "STT",
                    modifier = Modifier.weight(0.5f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    "Niên học học kỳ",
                    modifier = Modifier.weight(2f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    "HP chưa giảm",
                    modifier = Modifier.weight(1.2f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End
                )
                Text(
                    "Miễn giảm",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End
                )
                Text(
                    "Phải thu",
                    modifier = Modifier.weight(1.2f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End
                )
                Text(
                    "Đã thu",
                    modifier = Modifier.weight(1.2f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End
                )
                Text(
                    "Còn nợ",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End
                )
            }

            Divider()

            // Group by category
            val regularFees = data.data.tuitionFeeList.filter { it.groupName == "Thu Học Phí" }
            val retakeFees =
                data.data.tuitionFeeList.filter { it.groupName == "Thu Học Phí Học Lại" }

            // Regular tuition fees
            if (regularFees.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PTITColors.neutralSubtle)
                        .padding(12.dp)
                ) {
                    Text(
                        "Thu Học Phí",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }

                regularFees.forEachIndexed { index, fee ->
                    TuitionFeeRow(
                        index = index + 1,
                        fee = fee
                    )
                }

                // Subtotal for regular fees
                SubtotalRow(fees = regularFees)
            }

            // Retake fees
            if (retakeFees.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PTITColors.neutralSubtle)
                        .padding(12.dp)
                ) {
                    Text(
                        "Thu Học Phí Học Lại",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }

                retakeFees.forEachIndexed { index, fee ->
                    TuitionFeeRow(
                        index = index + 1,
                        fee = fee
                    )
                }

                // Subtotal for retake fees
                SubtotalRow(fees = retakeFees)
            }

            // Grand total
            GrandTotalRow(fees = data.data.tuitionFeeList)
        }
    }
}

@Composable
fun TuitionFeeRow(
    index: Int,
    fee: TuitionFeePerSemester
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(index.toString(), modifier = Modifier.weight(0.5f), fontSize = 12.sp)
        Text(fee.semesterName, modifier = Modifier.weight(2f), fontSize = 12.sp)
        Text(
            formatCurrency(fee.tuitionFee),
            modifier = Modifier.weight(1.2f),
            fontSize = 12.sp,
            textAlign = TextAlign.End
        )
        Text(
            formatCurrency(fee.discount),
            modifier = Modifier.weight(1f),
            fontSize = 12.sp,
            textAlign = TextAlign.End
        )
        Text(
            formatCurrency(fee.amountDue),
            modifier = Modifier.weight(1.2f),
            fontSize = 12.sp,
            textAlign = TextAlign.End
        )
        Text(
            formatCurrency(fee.amountPaid),
            modifier = Modifier.weight(1.2f),
            fontSize = 12.sp,
            textAlign = TextAlign.End
        )
        Text(
            formatCurrency(fee.amountOwed),
            modifier = Modifier.weight(1f),
            fontSize = 12.sp,
            textAlign = TextAlign.End,
            color = if (fee.amountOwed.toIntOrNull() ?: 0 > 0) PTITColors.redDefault else PTITColors.neutralDefault
        )
    }
    Divider()
}

@Composable
fun SubtotalRow(fees: List<TuitionFeePerSemester>) {
    val totalTuitionFee = fees.sumOf { it.tuitionFee.toLongOrNull() ?: 0 }
    val totalDiscount = fees.sumOf { it.discount.toLongOrNull() ?: 0 }
    val totalAmountDue = fees.sumOf { it.amountDue.toLongOrNull() ?: 0 }
    val totalAmountPaid = fees.sumOf { it.amountPaid.toLongOrNull() ?: 0 }
    val totalAmountOwed = fees.sumOf { it.amountOwed.toLongOrNull() ?: 0 }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PTITColors.warningContainer)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text("TỔNG", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text(
            formatCurrency(totalTuitionFee.toString()),
            modifier = Modifier.weight(1.2f),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            textAlign = TextAlign.End
        )
        Text(
            formatCurrency(totalDiscount.toString()),
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            textAlign = TextAlign.End
        )
        Text(
            formatCurrency(totalAmountDue.toString()),
            modifier = Modifier.weight(1.2f),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            textAlign = TextAlign.End
        )
        Text(
            formatCurrency(totalAmountPaid.toString()),
            modifier = Modifier.weight(1.2f),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            textAlign = TextAlign.End
        )
        Text(
            formatCurrency(totalAmountOwed.toString()),
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            textAlign = TextAlign.End,
            color = if (totalAmountOwed > 0) PTITColors.redDefault else PTITColors.neutralDefault
        )
    }
    Divider()
}

@Composable
fun GrandTotalRow(fees: List<TuitionFeePerSemester>) {
    val totalTuitionFee = fees.sumOf { it.tuitionFee.toLongOrNull() ?: 0 }
    val totalDiscount = fees.sumOf { it.discount.toLongOrNull() ?: 0 }
    val totalAmountDue = fees.sumOf { it.amountDue.toLongOrNull() ?: 0 }
    val totalAmountPaid = fees.sumOf { it.amountPaid.toLongOrNull() ?: 0 }
    val totalAmountOwed = fees.sumOf { it.amountOwed.toLongOrNull() ?: 0 }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PTITColors.neutralMuted)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(
            "TỔNG CỘNG",
            modifier = Modifier.weight(2f),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = PTITColors.info
        )
        Text(
            formatCurrency(totalTuitionFee.toString()),
            modifier = Modifier.weight(1.2f),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            textAlign = TextAlign.End,
            color = PTITColors.info
        )
        Text(
            formatCurrency(totalDiscount.toString()),
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            textAlign = TextAlign.End,
            color = PTITColors.info
        )
        Text(
            formatCurrency(totalAmountDue.toString()),
            modifier = Modifier.weight(1.2f),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            textAlign = TextAlign.End,
            color = PTITColors.info
        )
        Text(
            formatCurrency(totalAmountPaid.toString()),
            modifier = Modifier.weight(1.2f),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            textAlign = TextAlign.End,
            color = PTITColors.info
        )
        Text(
            formatCurrency(totalAmountOwed.toString()),
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            textAlign = TextAlign.End,
            color = if (totalAmountOwed > 0) PTITColors.redDefault else PTITColors.info
        )
    }
}

@Composable
fun DetailTuitionFeeTable(
    data: DetailTuitionFeeResponse?,
    modifier: Modifier = Modifier
) {
    if (data == null) return

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 1.dp
    ) {
        Column {
            // Payable items section
            if (data.data.payableList.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PTITColors.neutralMuted)
                        .padding(12.dp)
                ) {
                    Text(
                        "Chi tiết phải thu",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Payable table header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PTITColors.neutralSubtle)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "STT",
                        modifier = Modifier.weight(0.5f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        "Mã môn",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        "Diễn giải",
                        modifier = Modifier.weight(2.5f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        "Tín chỉ",
                        modifier = Modifier.weight(0.7f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.End
                    )
                    Text(
                        "Học phí",
                        modifier = Modifier.weight(1.2f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.End
                    )
                    Text(
                        "Miễn giảm",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.End
                    )
                    Text(
                        "Phải thu",
                        modifier = Modifier.weight(1.2f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.End
                    )
                }

                Divider()

                data.data.payableList.forEachIndexed { index, item ->
                    PayableItemRow(index = index + 1, item = item)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Paid items section
            if (data.data.paidList.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PTITColors.neutralMuted)
                        .padding(12.dp)
                ) {
                    Text(
                        "Chi tiết đã thu",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Paid table header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PTITColors.neutralSubtle)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "STT",
                        modifier = Modifier.weight(0.5f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        "Mã môn",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        "Diễn giải",
                        modifier = Modifier.weight(2.5f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        "Đã thu",
                        modifier = Modifier.weight(1.2f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.End
                    )
                    Text(
                        "Ngày thu",
                        modifier = Modifier.weight(1.5f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.End
                    )
                }

                Divider()

                data.data.paidList.forEachIndexed { index, item ->
                    PaidItemRow(index = index + 1, item = item)
                }
            }
        }
    }
}

@Composable
fun PayableItemRow(index: Int, item: PayableItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(index.toString(), modifier = Modifier.weight(0.5f), fontSize = 12.sp)
        Text(item.courseCode, modifier = Modifier.weight(1f), fontSize = 12.sp)
        Text(item.description, modifier = Modifier.weight(2.5f), fontSize = 12.sp)
        Text(
            item.credits,
            modifier = Modifier.weight(0.7f),
            fontSize = 12.sp,
            textAlign = TextAlign.End
        )
        Text(
            formatCurrency(item.tuitionFee),
            modifier = Modifier.weight(1.2f),
            fontSize = 12.sp,
            textAlign = TextAlign.End
        )
        Text(
            formatCurrency(item.discount),
            modifier = Modifier.weight(1f),
            fontSize = 12.sp,
            textAlign = TextAlign.End
        )
        Text(
            formatCurrency(item.amountDue),
            modifier = Modifier.weight(1.2f),
            fontSize = 12.sp,
            textAlign = TextAlign.End
        )
    }
    Divider()
}

@Composable
fun PaidItemRow(index: Int, item: PaidItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(index.toString(), modifier = Modifier.weight(0.5f), fontSize = 12.sp)
        Text(item.courseCode, modifier = Modifier.weight(1f), fontSize = 12.sp)
        Text(item.description, modifier = Modifier.weight(2.5f), fontSize = 12.sp)
        Text(
            formatCurrency(item.amountPaid),
            modifier = Modifier.weight(1.2f),
            fontSize = 12.sp,
            textAlign = TextAlign.End
        )
        Text(
            item.paymentDate,
            modifier = Modifier.weight(1.5f),
            fontSize = 12.sp,
            textAlign = TextAlign.End
        )
    }
    Divider()
}

fun formatCurrency(value: String): String {
    val number = value.toLongOrNull() ?: return "0"
    return String.format("%,d", number).replace(",", ".")
}
