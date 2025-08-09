package lamdx4.uis.ptithcm.ui.more.invoices

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import lamdx4.uis.ptithcm.data.model.Invoice
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun InvoicesScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    viewModel: InvoicesViewModel = hiltViewModel()
) {
    val invoicesResponse by viewModel.invoicesState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadInvoices()
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                val invoices = invoicesResponse?.data?.invoices ?: emptyList()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(invoices) { index, invoice ->
                        InvoiceCard(
                            index = index + 1,
                            invoice = invoice
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun InvoiceCard(index: Int, invoice: Invoice) {
    var expanded by remember { mutableStateOf(false) }

    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // Số thứ tự
            Text(
                text = "Hóa đơn #$index",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Số hóa đơn
            Text(
                text = "Số hóa đơn: ${invoice.invoiceNumber}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Số tiền
            Text(
                text = "Số tiền: ${"%,.0f".format(invoice.amount)} VND",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Ngày đóng
            Text(
                text = "Ngày đóng: ${formatDate(invoice.paymentDate)}",
                style = MaterialTheme.typography.bodyMedium
            )

            // Ghi chú
            if (invoice.note.isNotBlank()) {
                Text(
                    text = "Ghi chú: ${invoice.note}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Chi tiết mở rộng
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                Spacer(modifier = Modifier.height(8.dp))

                // Thông tin sinh viên
                Text("Mã SV: ${invoice.studentCode}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Tên sinh viên: ${invoice.fullName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "Ngày sinh: ${invoice.dateOfBirth}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text("Lớp: ${invoice.classCode}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Ngày lập phiếu: ${formatDate(invoice.invoiceDate)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text("Học kỳ: ${invoice.semester}", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(12.dp))

                // Nút tải file
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { /* TODO: tải PDF */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Tải File PDF")
                    }

                    Button(
                        onClick = { /* TODO: tải XML */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Tải File XML")
                    }
                }
            }
        }
    }
}

fun formatDate(dateString: String): String {
    return try {
        if (dateString.contains("T")) {
            val dateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
            dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        } else {
            val date = LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE)
            date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        }
    } catch (e: Exception) {
        dateString // Nếu parse lỗi thì trả về nguyên bản
    }
}