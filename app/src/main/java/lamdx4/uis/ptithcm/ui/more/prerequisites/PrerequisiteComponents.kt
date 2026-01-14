package lamdx4.uis.ptithcm.ui.more.prerequisites

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import lamdx4.uis.ptithcm.data.model.PrerequisiteSubject
import lamdx4.uis.ptithcm.data.model.PrerequisitesTypeResponse

@Composable
fun PrerequisiteTable(
    data: List<PrerequisiteSubject>,
    typeList: List<PrerequisitesTypeResponse>,
    selectedType: PrerequisitesTypeResponse?,
    onTypeSelected: (PrerequisitesTypeResponse) -> Unit,
    navController: NavController? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
    ) {
        // ---------------------------------------------------------
        // PHẦN 1: FILTER HEADER
        // ---------------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dropdown chọn loại
            FilterDropdown(
                types = typeList,
                selectedType = selectedType,
                onSelected = onTypeSelected
            )

            // Các nút Xuất Excel
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                OutlinedButton(
//                    onClick = { },
//                    contentPadding = PaddingValues(horizontal = 12.dp),
//                    shape = RoundedCornerShape(4.dp)
//                ) { Text("In") }

                Button(
                    onClick = {
                        val targetUrl = "https://uis.ptithcm.edu.vn/#/montienquyet"

                        // cần Encode URL vì nó có ký tự đặc biệt (#, /)
                        val encodedUrl = java.net.URLEncoder.encode(
                            targetUrl,
                            java.nio.charset.StandardCharsets.UTF_8.toString()
                        )

                        // Điều hướng
                        navController?.navigate("export_webview?url=$encodedUrl")
                    },
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = "Mở Web",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // ---------------------------------------------------------
        // PHẦN 2: NỘI DUNG
        // ---------------------------------------------------------
        if (data.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Không có dữ liệu",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Vui lòng chọn loại điều kiện khác",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            PrerequisiteListContent(data)
        }
    }
}

@Composable
fun PrerequisiteListContent(data: List<PrerequisiteSubject>) {
    Column {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(vertical = 12.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TableHeaderCell(text = "STT", weight = 0.6f, align = TextAlign.Center)
            TableHeaderCell(text = "Mã ĐK", weight = 1.2f)
            TableHeaderCell(text = "Tên môn ĐK", weight = 2f)
            TableHeaderCell(text = "Mã YC", weight = 1.2f)
            TableHeaderCell(text = "Tên môn YC", weight = 2f)
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.primary, thickness = 1.dp)

        // List
        if (data.isEmpty()) {
            EmptyDataView()
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(data) { index, item ->
                    TableRow(index = index + 1, item = item)
                    if (index < data.lastIndex) HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@Composable
fun TableRow(index: Int, item: PrerequisiteSubject) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TableCell(text = index.toString(), weight = 0.6f, align = TextAlign.Center, isBold = true)
        TableCell(
            text = item.subjectCode,
            weight = 1.2f,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TableCell(text = item.subjectName, weight = 2f, color = MaterialTheme.colorScheme.primary)
        TableCell(
            text = item.prerequisiteCode,
            weight = 1.2f,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TableCell(
            text = item.prerequisiteName,
            weight = 2f,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun FilterDropdown(
    types: List<PrerequisitesTypeResponse>,
    selectedType: PrerequisitesTypeResponse?,
    onSelected: (PrerequisitesTypeResponse) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        AssistChip(
            onClick = { expanded = true },
            label = {
                Text(
                    text = selectedType?.description ?: "Chọn loại",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            },
            trailingIcon = {
                // Icon mũi tên chỉ xuống để user biết là dropdown
                Icon(
                    imageVector = Icons.Default.ArrowDropDown, // Cần import androidx.compose.material.icons.filled.ArrowDropDown
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            types.forEach { type ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = type.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (type == selectedType) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        onSelected(type)
                        expanded = false
                    },
                    // Highlight item đang được chọn
                    leadingIcon = if (type == selectedType) {
                        {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else null
                )
            }
        }
    }
}

@Composable
fun RowScope.TableHeaderCell(text: String, weight: Float, align: TextAlign = TextAlign.Start) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = align
    )
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    align: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.colorScheme.onSurface,
    isBold: Boolean = false
) {
    Text(
        text = text,
        modifier = Modifier
            .weight(weight)
            .padding(horizontal = 2.dp),
        style = MaterialTheme.typography.bodySmall,
        fontWeight = if (isBold) FontWeight.SemiBold else FontWeight.Normal,
        color = color,
        textAlign = align,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun ErrorView(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Đã có lỗi xảy ra",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EmptyDataView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Không có dữ liệu điều kiện",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
