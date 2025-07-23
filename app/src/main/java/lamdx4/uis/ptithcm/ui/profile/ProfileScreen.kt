package lamdx4.uis.ptithcm.ui.profile

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import lamdx4.uis.ptithcm.ui.AppViewModel
import lamdx4.uis.ptithcm.ui.statistics.StatisticsViewModel
import lamdx4.uis.ptithcm.ui.statistics.StatisticsUiState
import lamdx4.uis.ptithcm.ui.components.GradeChart
import lamdx4.uis.ptithcm.data.model.CompleteStudentInfo
import lamdx4.uis.ptithcm.data.model.GradeStatistics
import lamdx4.uis.ptithcm.data.model.SemesterInfo
import kotlinx.coroutines.launch

// Utility function để chuyển đổi base64 thành bitmap
fun decodeBase64ToBitmap(base64String: String): android.graphics.Bitmap? {
    return try {
        // Remove data:image prefix if exists
        val cleanBase64 = if (base64String.startsWith("data:image")) {
            base64String.split(",")[1]
        } else {
            base64String
        }
        
        val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        null
    }
}

@Composable
fun ProfileScreen(
    appViewModel: AppViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel(),
    statisticsViewModel: StatisticsViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val userState by appViewModel.uiState.collectAsState()
    val statisticsState by statisticsViewModel.uiState.collectAsState()
    val accessToken = userState.accessToken
    val maSV = userState.maSV
    val profile = userState.profile

    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Load profile info if not loaded
    LaunchedEffect(maSV, accessToken) {
        if (profile == null && !loading && !maSV.isNullOrEmpty() && !accessToken.isNullOrEmpty()) {
            loading = true
            error = null
            coroutineScope.launch {
                try {
                    val result = profileViewModel.loadProfile(accessToken, maSV)
                    result?.let { appViewModel.setProfile(it) }
                } catch (e: Exception) {
                    error = "Không thể tải thông tin: ${e.message}"
                } finally {
                    loading = false
                }
            }
        }
    }

    // Load statistics when profile is available
    LaunchedEffect(accessToken, profile) {
        if (!accessToken.isNullOrEmpty() && profile != null) {
            statisticsViewModel.loadAvailableSemesters(accessToken)
        }
    }

    // Load academic result when semesters are available
    LaunchedEffect(statisticsState.availableSemesters, accessToken) {
        if (!accessToken.isNullOrEmpty() && statisticsState.availableSemesters.isNotEmpty()) {
            // Load academic result for the selected semester (or latest semester)
            val semesterToLoad = if (statisticsState.selectedSemester > 0) {
                statisticsState.selectedSemester
            } else {
                statisticsState.availableSemesters.maxByOrNull { it.hoc_ky }?.hoc_ky ?: 20242
            }
            statisticsViewModel.loadAcademicResult(accessToken, semesterToLoad)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        when {
            loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            profile != null -> {
                ProfileContent(
                    profile = profile,
                    statisticsState = statisticsState,
                    statisticsViewModel = statisticsViewModel,
                    accessToken = accessToken
                )
            }
            
            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error!!,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            
            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Không có thông tin sinh viên")
                }
            }
        }
    }
}

@Composable
fun ProfileContent(
    profile: CompleteStudentInfo,
    statisticsState: StatisticsUiState,
    statisticsViewModel: StatisticsViewModel,
    accessToken: String?
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        // Header với ảnh và thông tin cơ bản
        ProfileHeader(profile)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Thông tin cá nhân
        PersonalInfoSection(profile)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Thông tin khóa học
        AcademicInfoSection(profile)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Semester Selector & Biểu đồ điểm số
        SemesterChartSection(
            statisticsState = statisticsState,
            statisticsViewModel = statisticsViewModel,
            accessToken = accessToken
        )
    }
}

@Composable
fun ProfileHeader(profile: CompleteStudentInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ảnh đại diện
            if (!profile.image.isNullOrBlank()) {
                val bitmap = remember(profile.image) {
                    decodeBase64ToBitmap(profile.image!!)
                }
                
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Ảnh sinh viên",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Fallback to default avatar if base64 decoding fails
                    Card(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Avatar",
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Thông tin cơ bản
            Column {
                Text(
                    text = profile.ten_day_du,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = profile.ma_sv,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Text(
                    text = profile.hien_dien_sv,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (profile.hien_dien_sv == "Đang học") Color.Green else Color.Red
                )
            }
        }
    }
}

@Composable
fun PersonalInfoSection(profile: CompleteStudentInfo) {
    InfoCard(
        title = "Thông tin cá nhân",
        icon = Icons.Default.Person
    ) {
        InfoRow("Ngày sinh", profile.ngay_sinh, Icons.Default.DateRange)
        InfoRow("Giới tính", profile.gioi_tinh, Icons.Default.Person)
        InfoRow("Điện thoại", profile.dien_thoai, Icons.Default.Phone)
        InfoRow("CMND/CCCD", profile.so_cmnd, Icons.Default.Badge)
        InfoRow("Email", profile.email, Icons.Default.Email)
        if (profile.email2.isNotBlank()) {
            InfoRow("Email 2", profile.email2, Icons.Default.Email)
        }
        InfoRow("Nơi sinh", profile.noi_sinh, Icons.Default.LocationOn)
        InfoRow("Dân tộc", profile.dan_toc, Icons.Default.Group)
        InfoRow("Tôn giáo", profile.ton_giao, Icons.Default.Church)
        InfoRow("Hộ khẩu", profile.ho_khau_thuong_tru_gd, Icons.Default.Home)
    }
}

@Composable
fun AcademicInfoSection(profile: CompleteStudentInfo) {
    InfoCard(
        title = "Thông tin khóa học",
        icon = Icons.Default.School
    ) {
        InfoRow("Lớp", profile.lop, Icons.Default.Class)
        InfoRow("Ngành", profile.nganh, Icons.Default.Engineering)
        InfoRow("Chuyên ngành", profile.chuyen_nganh, Icons.Default.Science)
        InfoRow("Khoa", profile.khoa, Icons.Default.School)
        InfoRow("Bậc đào tạo", profile.bac_he_dao_tao, Icons.Default.Grade)
        InfoRow("Niên khóa", profile.nien_khoa, Icons.Default.CalendarMonth)
    }
}

@Composable
fun InfoCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            content()
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = label,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
fun StatisticsSection(
    statisticsViewModel: StatisticsViewModel,
    accessToken: String
) {
    val statisticsState by statisticsViewModel.uiState.collectAsState()
    
    // Load academic result when semester changes
    LaunchedEffect(statisticsState.selectedSemester, accessToken) {
        if (statisticsState.availableSemesters.isNotEmpty()) {
            statisticsViewModel.loadAcademicResult(accessToken, statisticsState.selectedSemester)
        }
    }
    
    InfoCard(
        title = "Thống kê học tập",
        icon = Icons.Default.Analytics
    ) {
        // Semester selector
        if (statisticsState.availableSemesters.isNotEmpty()) {
            SemesterSelector(
                selectedSemester = statisticsState.selectedSemester,
                availableSemesters = statisticsState.availableSemesters,
                loading = statisticsState.loadingSemesters,
                onSemesterSelected = { semester ->
                    statisticsViewModel.setSemester(semester)
                    statisticsViewModel.loadAcademicResult(accessToken, semester)
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Statistics content
        when {
            statisticsState.loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            statisticsState.error != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = statisticsState.error!!,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            statisticsState.academicResult != null -> {
                StatisticsContent(statisticsState.academicResult!!)
            }
            
            else -> {
                Text(
                    text = "Chưa có dữ liệu thống kê",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemesterSelector(
    selectedSemester: Int,
    availableSemesters: List<SemesterInfo>,
    loading: Boolean = false,
    onSemesterSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded && !loading }
    ) {
        OutlinedTextField(
            value = if (loading) {
                "Đang tải..."
            } else {
                availableSemesters.find { it.hoc_ky == selectedSemester }?.ten_hoc_ky ?: "Chọn học kỳ"
            },
            onValueChange = {},
            readOnly = true,
            enabled = !loading,
            label = { Text("Học kỳ") },
            trailingIcon = { 
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableSemesters.forEach { semester ->
                DropdownMenuItem(
                    text = { 
                        Column {
                            Text(semester.ten_hoc_ky)
                            if (semester.so_mon > 0) {
                                Text(
                                    text = "${semester.so_mon} môn • ĐTB: ${String.format("%.2f", semester.diem_trung_binh ?: 0.0)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    onClick = {
                        onSemesterSelected(semester.hoc_ky)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun StatisticsContent(data: lamdx4.uis.ptithcm.data.model.AcademicResultData) {
    Column {
        // Overview stats
        if (data.tong_so_mon != null || data.diem_trung_binh != null) {
            OverviewStats(data)
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Grade chart
        if (data.thong_ke_diem != null) {
            GradeChart(data.thong_ke_diem!!)
        }
    }
}

@Composable
fun OverviewStats(data: lamdx4.uis.ptithcm.data.model.AcademicResultData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                label = "Tổng môn",
                value = "${data.tong_so_mon ?: 0}",
                icon = Icons.Default.Book
            )
            
            VerticalDivider(
                modifier = Modifier.height(50.dp),
                thickness = 1.dp
            )
            
            StatItem(
                label = "Tín chỉ",
                value = "${data.tong_so_tin_chi ?: 0}",
                icon = Icons.Default.School
            )
            
            VerticalDivider(
                modifier = Modifier.height(50.dp),
                thickness = 1.dp
            )
            
            StatItem(
                label = "ĐTB",
                value = String.format("%.2f", data.diem_trung_binh ?: 0.0),
                icon = Icons.Default.Grade,
                valueColor = when {
                    (data.diem_trung_binh ?: 0.0) >= 8.0 -> Color.Green
                    (data.diem_trung_binh ?: 0.0) >= 6.5 -> Color.Blue
                    else -> Color.Red
                }
            )
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    icon: ImageVector,
    valueColor: Color = MaterialTheme.colorScheme.onSecondaryContainer
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun GradeChart(statistics: GradeStatistics) {
    Column {
        Text(
            text = "Phân bố điểm",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        val density = LocalDensity.current
        val gradeData = listOf(
            "A" to statistics.so_mon_A,
            "B" to statistics.so_mon_B,
            "C" to statistics.so_mon_C,
            "D" to statistics.so_mon_D,
            "F" to statistics.so_mon_F
        )
        val maxValue = gradeData.maxOfOrNull { it.second } ?: 1
        
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val barWidth = canvasWidth / (gradeData.size * 1.5f)
            val maxBarHeight = canvasHeight * 0.7f
            
            gradeData.forEachIndexed { index, (grade, count) ->
                val barHeight = if (maxValue > 0) (count.toFloat() / maxValue) * maxBarHeight else 0f
                val startX = (index * barWidth * 1.5f) + barWidth * 0.25f
                
                val color = when (grade) {
                    "A" -> androidx.compose.ui.graphics.Color(0xFF4CAF50) // Green
                    "B" -> androidx.compose.ui.graphics.Color(0xFF2196F3) // Blue
                    "C" -> androidx.compose.ui.graphics.Color(0xFFFFD700) // Gold
                    "D" -> androidx.compose.ui.graphics.Color(0xFFFF8C00) // Orange
                    else -> androidx.compose.ui.graphics.Color(0xFFFF5722) // Red
                }
                
                if (barHeight > 0) {
                    drawRect(
                        color = color,
                        topLeft = Offset(startX, canvasHeight - barHeight - 30.dp.toPx()),
                        size = Size(barWidth, barHeight)
                    )
                }
                
                drawContext.canvas.nativeCanvas.apply {
                    // Draw grade label
                    drawText(
                        grade,
                        startX + barWidth / 2,
                        canvasHeight - 10.dp.toPx(),
                        android.graphics.Paint().apply {
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = with(density) { 12.sp.toPx() }
                            isFakeBoldText = true
                        }
                    )
                    
                    // Draw count on top of bar
                    if (count > 0) {
                        drawText(
                            count.toString(),
                            startX + barWidth / 2,
                            canvasHeight - barHeight - 35.dp.toPx(),
                            android.graphics.Paint().apply {
                                textAlign = android.graphics.Paint.Align.CENTER
                                textSize = with(density) { 10.sp.toPx() }
                                isFakeBoldText = true
                            }
                        )
                    }
                }
            }
        }
        
        // Grade summary
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items(gradeData) { (grade, count) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = grade,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemesterChartSection(
    statisticsState: StatisticsUiState,
    statisticsViewModel: StatisticsViewModel,
    accessToken: String?
) {
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    Column {
        // Semester selector
        if (statisticsState.availableSemesters.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Chọn học kỳ",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        val selectedSemester = statisticsState.availableSemesters.find { 
                            it.hoc_ky == statisticsState.selectedSemester 
                        } ?: statisticsState.availableSemesters.maxByOrNull { it.hoc_ky }
                        
                        OutlinedTextField(
                            value = selectedSemester?.let { "${it.ten_hoc_ky} (${it.nam_hoc ?: ""})" } ?: "Chọn học kỳ",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            statisticsState.availableSemesters.sortedByDescending { it.hoc_ky }.forEach { semester ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = semester.ten_hoc_ky,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            if (!semester.nam_hoc.isNullOrEmpty()) {
                                                Text(
                                                    text = semester.nam_hoc,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        expanded = false
                                        statisticsViewModel.setSemester(semester.hoc_ky)
                                        if (!accessToken.isNullOrEmpty()) {
                                            coroutineScope.launch {
                                                statisticsViewModel.loadAcademicResult(accessToken, semester.hoc_ky)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Biểu đồ điểm số
        if (statisticsState.academicResult?.ds_du_lieu?.isNotEmpty() == true) {
            GradeChart(
                subjects = statisticsState.academicResult.ds_du_lieu!!,
                modifier = Modifier.fillMaxWidth()
            )
        } else if (statisticsState.loading) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Đang tải dữ liệu thống kê...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else if (statisticsState.error != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "Lỗi tải dữ liệu: ${statisticsState.error}",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Không có dữ liệu thống kê",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Vui lòng chọn học kỳ để xem biểu đồ điểm",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}