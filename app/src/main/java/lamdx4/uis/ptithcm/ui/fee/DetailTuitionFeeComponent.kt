package lamdx4.uis.ptithcm.ui.fee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
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
import lamdx4.uis.ptithcm.data.model.DetailTuitionFeeResponse
import lamdx4.uis.ptithcm.data.model.PaidItem
import lamdx4.uis.ptithcm.data.model.PayableItem
import lamdx4.uis.ptithcm.ui.theme.PTITColors

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
                        text = "Chi tiết phải thu",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = PTITColors.neutralEmphasis
                    )
                }

                // Payable table header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PTITColors.neutralSubtle)
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STT",
                        modifier = Modifier.weight(0.5f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "Mã môn",
                        modifier = Modifier.weight(0.9f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "Tên môn",
                        modifier = Modifier
                            .weight(1.85f)
                            .padding(start = 4.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "TC",
                        modifier = Modifier.weight(0.5f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Miễn giảm",
                        modifier = Modifier.weight(0.9f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = "Phải thu",
                        modifier = Modifier.weight(1.1f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.End
                    )
                }

                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)

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
                        text = "Chi tiết đã thu",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = PTITColors.neutralEmphasis
                    )
                }

                // Paid table header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PTITColors.neutralSubtle)
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STT",
                        modifier = Modifier.weight(0.5f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "Mã môn",
                        modifier = Modifier.weight(0.9f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "Tên môn",
                        modifier = Modifier
                            .weight(1.9f)
                            .padding(start = 4.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "Đã thu",
                        modifier = Modifier.weight(1.05f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Ngày thu",
                        modifier = Modifier.weight(1.3f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }

                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)

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
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = index.toString(),
            modifier = Modifier.weight(0.5f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start
        )
        Text(
            text = item.courseCode,
            modifier = Modifier.weight(0.9f),
            fontSize = 12.sp,
            textAlign = TextAlign.Start
        )
        Text(
            text = item.description,
            modifier = Modifier
                .weight(1.85f)
                .padding(start = 4.dp),
            fontSize = 12.sp,
            textAlign = TextAlign.Start,
            lineHeight = 16.sp
        )

        Text(
            text = item.credits,
            modifier = Modifier.weight(0.5f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
        )

        val discountVal = item.discount.toLongOrNull() ?: 0
        Text(
            text = formatCurrency(item.discount),
            modifier = Modifier.weight(0.9f),
            fontSize = 12.sp,
            textAlign = TextAlign.End,
            color = if (discountVal > 0) PTITColors.success else PTITColors.neutralDefault
        )

        val amountDueVal = item.amountDue.toLongOrNull() ?: 0
        Text(
            text = formatCurrency(item.amountDue),
            modifier = Modifier.weight(1.1f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            color = if (amountDueVal > 0) PTITColors.redDefault else PTITColors.success
        )
    }
    HorizontalDivider(thickness = 0.5.dp, color = PTITColors.neutralMuted)
}

@Composable
fun PaidItemRow(index: Int, item: PaidItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = index.toString(),
            modifier = Modifier.weight(0.35f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start
        )
        Text(
            text = item.courseCode,
            modifier = Modifier.weight(0.9f),
            fontSize = 12.sp,
            textAlign = TextAlign.Start
        )
        Text(
            text = item.description,
            modifier = Modifier
                .weight(2.0f)
                .padding(start = 4.dp),
            fontSize = 12.sp,
            textAlign = TextAlign.Start,
            lineHeight = 16.sp
        )
        Text(
            text = formatCurrency(item.amountPaid),
            modifier = Modifier.weight(1.1f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            color = PTITColors.success
        )
        Text(
            text = item.paymentDate,
            modifier = Modifier.weight(1.3f),
            fontSize = 11.sp,
            textAlign = TextAlign.End,
            color = PTITColors.neutralDefault,
            lineHeight = 14.sp
        )
    }
    HorizontalDivider(thickness = 0.5.dp, color = PTITColors.neutralMuted)
}
