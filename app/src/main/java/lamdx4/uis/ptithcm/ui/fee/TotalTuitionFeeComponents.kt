package lamdx4.uis.ptithcm.ui.fee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lamdx4.uis.ptithcm.data.model.TotalTuitionFeeResponse
import lamdx4.uis.ptithcm.data.model.TuitionFeePerSemester
import lamdx4.uis.ptithcm.ui.theme.PTITColors

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
                    .padding(vertical = 12.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STT",
                    modifier = Modifier.weight(0.35f),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Học kỳ",
                    modifier = Modifier.weight(1.35f),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Học phí",
                    modifier = Modifier.weight(1.3f),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Giảm",
                    modifier = Modifier.weight(1.0f),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Đã thu",
                    modifier = Modifier.weight(1.3f),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
            }

            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)

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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = index.toString(),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(0.35f),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start
            )
            Text(
                text = fee.semesterName,
                modifier = Modifier.weight(1.35f),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start,
            )
            Text(
                text = formatCurrency(fee.tuitionFee),
                modifier = Modifier.weight(1.3f),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false
            )
            Text(
                text = formatCurrency(fee.discount),
                modifier = Modifier.weight(1.0f),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false
            )

            val amountPaidVal = fee.amountPaid.toLongOrNull() ?: 0
            Text(
                text = formatCurrency(fee.amountPaid),
                modifier = Modifier.weight(1.3f),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (amountPaidVal > 0) PTITColors.success else MaterialTheme.colorScheme.onSurface
                ),
                fontWeight = if (amountPaidVal > 0) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.End,
                maxLines = 1,
                softWrap = false
            )
        }
    }
    HorizontalDivider(thickness = 0.5.dp, color = PTITColors.neutralMuted)
}

@Composable
fun SubtotalRow(fees: List<TuitionFeePerSemester>) {
    val totalTuitionFee = fees.sumOf { it.tuitionFee.toLongOrNull() ?: 0 }
    val totalDiscount = fees.sumOf { it.discount.toLongOrNull() ?: 0 }
    val totalAmountPaid = fees.sumOf { it.amountPaid.toLongOrNull() ?: 0 }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PTITColors.warningContainer)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "TỔNG",
            modifier = Modifier.weight(1.7f),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = PTITColors.onWarningContainer
        )
        Text(
            text = formatCurrency(totalTuitionFee.toString()),
            modifier = Modifier.weight(1.3f),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            color = PTITColors.onWarningContainer
        )
        Text(
            text = formatCurrency(totalDiscount.toString()),
            modifier = Modifier.weight(1.0f),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            color = PTITColors.onWarningContainer
        )
        Text(
            text = formatCurrency(totalAmountPaid.toString()),
            modifier = Modifier.weight(1.3f),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            color = PTITColors.onWarningContainer
        )
    }
    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
}

@Composable
fun GrandTotalRow(fees: List<TuitionFeePerSemester>) {
    val totalTuitionFee = fees.sumOf { it.tuitionFee.toLongOrNull() ?: 0 }
    val totalDiscount = fees.sumOf { it.discount.toLongOrNull() ?: 0 }
    val totalAmountPaid = fees.sumOf { it.amountPaid.toLongOrNull() ?: 0 }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PTITColors.infoContainer)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "TỔNG CỘNG",
            modifier = Modifier.weight(1.7f),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = PTITColors.onInfoContainer,
            textAlign = TextAlign.Center
        )

        Text(
            text = formatCurrency(totalTuitionFee.toString()),
            modifier = Modifier.weight(1.3f),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            color = PTITColors.onInfoContainer,
            maxLines = 1,
            softWrap = false
        )

        Text(
            text = formatCurrency(totalDiscount.toString()),
            modifier = Modifier.weight(1.0f),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            color = PTITColors.onInfoContainer,
            maxLines = 1,
            softWrap = false
        )

        Text(
            text = formatCurrency(totalAmountPaid.toString()),
            modifier = Modifier.weight(1.3f),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            color = PTITColors.onInfoContainer,
            maxLines = 1,
            softWrap = false
        )
    }
}
