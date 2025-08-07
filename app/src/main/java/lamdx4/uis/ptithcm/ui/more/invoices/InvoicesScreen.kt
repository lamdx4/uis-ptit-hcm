package lamdx4.uis.ptithcm.ui.more.invoices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import lamdx4.uis.ptithcm.ui.theme.PTITColors

data class Invoice(
    val id: String,
    val title: String,
    val amount: Long,
    val date: String,
    val status: InvoiceStatus,
    val description: String
)

enum class InvoiceStatus {
    PAID, PENDING, OVERDUE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesScreen(
    navController: NavController? = null,
    modifier: Modifier = Modifier
) {
    val invoices = remember {
        listOf(
            Invoice("INV001", "Học phí HK1 2024-2025", 5000000, "15/07/2025", InvoiceStatus.PAID, "Học phí học kỳ 1 năm học 2024-2025"),
            Invoice("INV002", "Phí bảo hiểm y tế", 500000, "20/07/2025", InvoiceStatus.PENDING, "Bảo hiểm y tế sinh viên năm 2025"),
            Invoice("INV003", "Phí ký túc xá", 2000000, "10/07/2025", InvoiceStatus.OVERDUE, "Phí ở ký túc xá 6 tháng"),
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hóa đơn điện tử", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(invoices) { invoice ->
                InvoiceCard(invoice)
            }
        }
    }
}

@Composable
private fun InvoiceCard(invoice: Invoice) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = invoice.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                StatusChip(invoice.status)
            }
            Text(
                text = "${String.format("%,d", invoice.amount)} VNĐ",
                style = MaterialTheme.typography.headlineSmall,
                color = PTITColors.redDefault
            )
            Text(text = invoice.date, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun StatusChip(status: InvoiceStatus) {
    val (text, color) = when (status) {
        InvoiceStatus.PAID -> "Đã thanh toán" to PTITColors.success
        InvoiceStatus.PENDING -> "Chờ thanh toán" to PTITColors.warning
        InvoiceStatus.OVERDUE -> "Quá hạn" to PTITColors.redDefault
    }
    
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
