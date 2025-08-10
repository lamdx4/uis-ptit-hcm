package lamdx4.uis.ptithcm.ui.more.invoices

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import lamdx4.uis.ptithcm.data.model.Invoice
import lamdx4.uis.ptithcm.util.downloadFile
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/* ------- Screen ------- */

@Composable
fun InvoicesScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    viewModel: InvoicesViewModel = hiltViewModel()
) {
    val invoicesResponse by viewModel.invoicesState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadInvoices() }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            errorMessage != null -> Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )

            else -> {
                val invoices = invoicesResponse?.data?.invoices ?: emptyList()
                if (invoices.isEmpty()) {
                    Text(
                        "Không có hóa đơn",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(
                            items = invoices,
                            key = { _, iv -> iv.invoiceCode ?: (iv.invoiceNumber + iv.studentCode) }
                        ) { index, invoice ->
                            InvoiceCard(index + 1, invoice)
                        }
                    }
                }
            }
        }
    }
}

/* ------- Card ------- */

private const val SHOW_NOTE_IN_COLLAPSED = true
private const val SHOW_CODE_IN_COLLAPSED = false

@Composable
fun InvoiceCard(
    index: Int,
    invoice: Invoice
) {
    var expanded by remember { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(if (expanded) 180f else 0f, label = "arrowRot")

    val context = LocalContext.current

    val code = invoice.invoiceCode ?: ""
    val pdfUrl = "https://www.meinvoice.vn/tra-cuu/downloadhandler.ashx?type=pdf&code=$code"
    val xmlUrl = "https://www.meinvoice.vn/tra-cuu/downloadhandler.ashx?type=xml&code=$code"
    val baseFileName = "hoa_don_${invoice.invoiceNumber}"

    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale("vi", "VN")) }
    val amountFormatted = runCatching { currencyFormatter.format(invoice.amount ?: 0.0) }.getOrDefault("0")

    val shape = RoundedCornerShape(12.dp)
    val outline = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, outline, shape),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            // Row 1: badge + title + expand
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IndexBadge(index)
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Hóa đơn ${invoice.invoiceNumber}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = if (expanded) "Thu gọn" else "Mở rộng",
                        modifier = Modifier.rotate(arrowRotation)
                    )
                }
            }

            // Row 2: date + semester + amount (end)
            Spacer(Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val dateStr = formatDate(invoice.paymentDate)
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (invoice.semester?.toString()?.isNotBlank() == true) {
                    Spacer(Modifier.width(8.dp))
                    SemesterChip(text = "HK ${invoice.semester}")
                }
                Spacer(Modifier.weight(1f))
                AmountInline(amountFormatted)
            }

            // Optional: note + code in collapsed
            if (SHOW_NOTE_IN_COLLAPSED && invoice.note.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = invoice.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (SHOW_CODE_IN_COLLAPSED && code.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = code,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(
                        thickness = DividerDefaults.Thickness,
                        color = DividerDefaults.color.copy(alpha = 0.4f)
                    )
                    Spacer(Modifier.height(12.dp))

                    DetailSection(invoice = invoice, code = code)

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                downloadFile(
                                    context = context,
                                    url = pdfUrl,
                                    fileName = "$baseFileName.pdf"
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(6.dp))
                            Text("PDF")
                        }
                        ElevatedButton(
                            onClick = {
                                downloadFile(
                                    context = context,
                                    url = xmlUrl,
                                    fileName = "$baseFileName.xml"
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Description, null)
                            Spacer(Modifier.width(6.dp))
                            Text("XML")
                        }
                        // Nếu muốn nút tải cả hai thì bỏ comment:
                        /*
                        Button(
                            onClick = {
                                downloadFile(context, pdfUrl, "$baseFileName.pdf")
                                downloadFile(context, xmlUrl, "$baseFileName.xml")
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.FileDownload, null)
                            Spacer(Modifier.width(6.dp))
                            Text("Cả hai")
                        }
                        */
                    }
                }
            }
        }
    }
}

/* ------- Sub components ------- */

@Composable
private fun IndexBadge(index: Int) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = index.toString(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AmountInline(amount: String) {
    Text(
        text = "$amount VND",
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun SemesterChip(text: String) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun DetailSection(invoice: Invoice, code: String) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        DetailRow("Mã hóa đơn", code)
        DetailRow("Mã SV", invoice.studentCode)
        DetailRow("Tên SV", invoice.fullName)
        DetailRow("Ngày sinh", invoice.dateOfBirth)
        DetailRow("Lớp", invoice.classCode)
        DetailRow("Ngày lập", formatDate(invoice.invoiceDate))
        DetailRow("Học kỳ", invoice.semester?.toString().orEmpty())
    }
}

@Composable
private fun DetailRow(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Row {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(90.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/* ------- Utils ------- */

fun formatDate(dateString: String): String {
    return try {
        if (dateString.contains("T")) {
            val dt = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
            dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        } else {
            val d = LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE)
            d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        }
    } catch (e: Exception) {
        Log.e("InvoicesScreen", "Failed to parse date: $dateString", e)
        "Invalid date"
    }
}