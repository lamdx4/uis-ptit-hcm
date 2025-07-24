package lamdx4.uis.ptithcm.ui.profile

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.offset
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.R
import lamdx4.uis.ptithcm.ui.AppViewModel
import lamdx4.uis.ptithcm.ui.statistics.StatisticsViewModel
import lamdx4.uis.ptithcm.ui.statistics.StatisticsUiState
import lamdx4.uis.ptithcm.ui.components.GradeChart
import lamdx4.uis.ptithcm.data.model.CompleteStudentInfo
import lamdx4.uis.ptithcm.ui.theme.PTITColors
import kotlin.text.*

// Utility function để chuyển đổi base64 thành bitmap
fun decodeBase64ToBitmap(base64String: String): android.graphics.Bitmap? {
    return try {
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
    appViewModel: AppViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
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

    // Auto-load default semester data after semesters are loaded
    LaunchedEffect(statisticsState.availableSemesters, accessToken) {
        if (!accessToken.isNullOrEmpty() && 
            statisticsState.availableSemesters.isNotEmpty() && 
            statisticsState.academicResult == null &&
            !statisticsState.loading) {
            
            // Auto-load the first/current semester
            val defaultSemester = statisticsState.availableSemesters.maxByOrNull { it.hoc_ky }
            defaultSemester?.let { semester ->
                coroutineScope.launch {
                    statisticsViewModel.loadAcademicResult(accessToken, semester.hoc_ky)
                }
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )

        when {
            loading -> LoadingState()
            profile != null -> {
                ImprovedProfileContent(
                    profile = profile,
                    statisticsState = statisticsState,
                    statisticsViewModel = statisticsViewModel,
                    accessToken = accessToken
                )
            }
            error != null -> ErrorState(error = error!!)
            else -> EmptyState()
        }
    }
}

@Composable
private fun ImprovedProfileContent(
    profile: CompleteStudentInfo,
    statisticsState: StatisticsUiState,
    statisticsViewModel: StatisticsViewModel,
    accessToken: String?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header Section
        item {
            ProfileHeaderCard(profile)
        }

        // Quick Stats
        item {
            QuickStatsSection(profile, statisticsState)
        }

        // Today's Schedule Section
        item {
            TodayScheduleSection(statisticsState)
        }

        // Notifications Section
        item {
            NotificationsSection(statisticsState)
        }

        // Tabbed Information Section
        item {
            TabbedInformationSection(profile)
        }

        // Statistics Section với improved layout
        item {
            ImprovedStatisticsSection(
                statisticsState = statisticsState,
                statisticsViewModel = statisticsViewModel,
                accessToken = accessToken
            )
        }
    }
}

@Composable
private fun ProfileHeaderCard(profile: CompleteStudentInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box {
            // Decorative background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Enhanced Avatar
                    EnhancedAvatar(profile.image)

                    // Student Info
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = profile.ten_day_du,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        StudentIdChip(profile.ma_sv)
                        StatusChip(profile.hien_dien_sv)
                    }
                }
            }
        }
    }
}

@Composable
private fun EnhancedAvatar(imageBase64: String?) {
    Box {
        if (!imageBase64.isNullOrBlank()) {
            val bitmap = remember(imageBase64) {
                decodeBase64ToBitmap(imageBase64)
            }

            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Ảnh sinh viên",
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .border(
                            4.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
                                )
                            ),
                            CircleShape
                        ),
                    contentScale = ContentScale.Crop
                )
            } else {
                DefaultAvatarEnhanced()
            }
        } else {
            DefaultAvatarEnhanced()
        }

        // Status indicator
        Surface(
            modifier = Modifier
                .size(28.dp)
                .align(Alignment.BottomEnd),
            shape = CircleShape,
            color = PTITColors.success,
            border = BorderStroke(3.dp, MaterialTheme.colorScheme.surface)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Verified,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun DefaultAvatarEnhanced() {
    Surface(
        modifier = Modifier.size(110.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        border = BorderStroke(
            4.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
                )
            )
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Avatar",
                modifier = Modifier.size(55.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun StudentIdChip(studentId: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.Badge,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = studentId,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val isActive = status == "Đang học"
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isActive) PTITColors.successContainer else PTITColors.warningContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = if (isActive) PTITColors.success else PTITColors.warning,
                        shape = CircleShape
                    )
            )
            Text(
                text = status,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = if (isActive) PTITColors.onSuccessContainer else PTITColors.onWarningContainer
            )
        }
    }
}

@Composable
private fun QuickStatsSection(
    profile: CompleteStudentInfo,
    statisticsState: StatisticsUiState
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        item {
            QuickStatCard(
                icon = Icons.Default.School,
                label = "Khoa",
                value = profile.khoa.take(15) + if (profile.khoa.length > 15) "..." else "",
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            QuickStatCard(
                icon = Icons.Default.Class,
                label = "Lớp",
                value = profile.lop,
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        item {
            QuickStatCard(
                icon = Icons.Default.Engineering,
                label = "Ngành",
                value = profile.nganh.take(12) + if (profile.nganh.length > 12) "..." else "",
                color = MaterialTheme.colorScheme.secondary
            )
        }

        item {
            QuickStatCard(
                icon = Icons.Default.Grade,
                label = "ĐTB",
                value = statisticsState.academicResult?.diem_trung_binh?.let {
                    String.format("%.2f", it)
                } ?: "--",
                color = PTITColors.success
            )
        }
    }
}

@Composable
private fun QuickStatCard(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = color.copy(alpha = 0.15f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = label,
                        modifier = Modifier.size(20.dp),
                        tint = color
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TabbedInformationSection(profile: CompleteStudentInfo) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column {
            // Tab Row
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    if (tabPositions.isNotEmpty()) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            color = MaterialTheme.colorScheme.primary,
                            height = 3.dp
                        )
                    }
                }
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Thông tin cá nhân",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Thông tin khóa học",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Pager Content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                when (page) {
                    0 -> PersonalInfoContent(profile)
                    1 -> AcademicInfoContent(profile)
                }
            }
        }
    }
}

@Composable
private fun PersonalInfoContent(profile: CompleteStudentInfo) {
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Grid layout cho thông tin cá nhân
        val personalInfoItems = listOf(
            Triple("Ngày sinh", profile.ngay_sinh, Icons.Default.Cake),
            Triple("Giới tính", profile.gioi_tinh, Icons.Default.Wc),
            Triple("Điện thoại", profile.dien_thoai, Icons.Default.Phone),
            Triple("CMND/CCCD", profile.so_cmnd, Icons.Default.Badge),
            Triple("Email", profile.email, Icons.Default.Email),
            Triple("Nơi sinh", profile.noi_sinh, Icons.Default.LocationOn),
            Triple("Dân tộc", profile.dan_toc, Icons.Default.Groups),
            Triple("Tôn giáo", profile.ton_giao, Icons.Default.AccountBalance)
        )

        personalInfoItems.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { (label, value, icon) ->
                    CompactInfoCard(
                        label = label,
                        value = value,
                        icon = icon,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill remaining space if odd number of items
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // Full width items
        if (profile.email2.isNotBlank()) {
            CompactInfoCard(
                label = "Email 2",
                value = profile.email2,
                icon = Icons.Default.AlternateEmail,
                modifier = Modifier.fillMaxWidth()
            )
        }

        CompactInfoCard(
            label = "Hộ khẩu thường trú",
            value = profile.ho_khau_thuong_tru_gd,
            icon = Icons.Default.Home,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun AcademicInfoContent(profile: CompleteStudentInfo) {
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val academicInfoItems = listOf(
            Triple("Lớp", profile.lop, Icons.Default.Class),
            Triple("Bậc đào tạo", profile.bac_he_dao_tao, Icons.Default.Grade),
            Triple("Chuyên ngành", profile.chuyen_nganh, Icons.Default.Science),
            Triple("Niên khóa", profile.nien_khoa, Icons.Default.CalendarMonth)
        )

        academicInfoItems.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { (label, value, icon) ->
                    CompactInfoCard(
                        label = label,
                        value = value,
                        icon = icon,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // Full width items
        CompactInfoCard(
            label = "Ngành",
            value = profile.nganh,
            icon = Icons.Default.Engineering,
            modifier = Modifier.fillMaxWidth()
        )

        CompactInfoCard(
            label = "Khoa",
            value = profile.khoa,
            icon = Icons.Default.School,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CompactInfoCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = label,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = value.ifBlank { "Chưa cập nhật" },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (value.isBlank())
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                else
                    MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImprovedStatisticsSection(
    statisticsState: StatisticsUiState,
    statisticsViewModel: StatisticsViewModel,
    accessToken: String?
) {
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = PTITColors.info.copy(alpha = 0.1f)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Analytics,
                            contentDescription = null,
                            tint = PTITColors.info,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = "Thống kê học tập",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Semester selector với improved spacing
            if (statisticsState.availableSemesters.isNotEmpty()) {
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
                        label = { Text("Học kỳ") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        statisticsState.availableSemesters.sortedByDescending { it.hoc_ky }.forEach { semester ->
                            DropdownMenuItem(
                                text = {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = semester.ten_hoc_ky,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium
                                        )
                                        if (!semester.nam_hoc.isNullOrEmpty()) {
                                            Text(
                                                text = semester.nam_hoc,
                                                style = MaterialTheme.typography.bodyMedium,
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

            // Chart content với improved spacing
            when {
                statisticsState.loading || statisticsState.loadingSemesters -> {
                    ChartLoadingState()
                }

                statisticsState.error != null -> {
                    ChartErrorState(statisticsState.error)
                }

                statisticsState.availableSemesters.isEmpty() -> {
                    ChartErrorState("Không thể tải danh sách học kỳ")
                }

                statisticsState.academicResult?.ds_du_lieu?.isNotEmpty() == true -> {
                    // Simplified chart with maximum space
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Chart title - minimal design
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.BarChart,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Biểu đồ điểm số các môn học",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        // Chart - maximum space with minimal padding
                            GradeChart(
                                subjects = statisticsState.academicResult.ds_du_lieu,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(400.dp) // Increased height significantly
                                    .padding(8.dp) // Minimal padding
                            )

                        // Chart info - compact footer
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${statisticsState.academicResult.ds_du_lieu.size} môn học",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            statisticsState.academicResult.diem_trung_binh?.let { dtb ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = stringResource(
                                            R.string.tb,
                                            String.format("%.2f", dtb)
                                        ),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                else -> {
                    ChartEmptyState()
                }
            }
        }
    }
}

// Loading, Error, Empty states remain the same as before
@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )
            Text(
                text = "Đang tải thông tin sinh viên...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorState(error: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = "Có lỗi xảy ra",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.PersonOff,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Không có thông tin sinh viên",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ChartLoadingState() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp), // Match chart height
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(60.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 6.dp
                )
                Text(
                    text = "Đang tải biểu đồ...",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun ChartErrorState(error: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp), // Match chart height
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Default.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Lỗi tải dữ liệu",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChartEmptyState() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp), // Match chart height
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Icon(
                    Icons.Default.BarChart,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Không có dữ liệu",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Chọn học kỳ để xem biểu đồ điểm",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayScheduleSection(statisticsState: StatisticsUiState) {
    val todaySchedule = statisticsState.academicResult?.ds_tkb_trong_ngay

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Lịch học hôm nay",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (todaySchedule.isNullOrEmpty()) {
                // Empty state
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EventBusy,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Không có lịch học hôm nay",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Schedule items
                todaySchedule.forEach { schedule ->
                    ScheduleItemCard(schedule)
                }
            }
        }
    }
}

@Composable
private fun ScheduleItemCard(schedule: lamdx4.uis.ptithcm.data.model.ScheduleToday) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Time indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = schedule.ten_thu,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = schedule.thoi_gian,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Subject info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = schedule.ten_mon,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = schedule.phong_hoc,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                schedule.ma_mon_hoc?.let { maMonHoc ->
                    Text(
                        text = maMonHoc,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Attendance info if available
            schedule.tong_so_sv?.let { total ->
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "${schedule.so_sv_co_mat ?: 0}/$total",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Có mặt",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationsSection(statisticsState: StatisticsUiState) {
    val unreadCount = statisticsState.academicResult?.sl_thong_bao_chua_doc ?: 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Notification icon with badge
            Box {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = if (unreadCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(28.dp)
                )
                
                if (unreadCount > 0) {
                    Surface(
                        modifier = Modifier
                            .offset(x = 8.dp, y = (-8).dp)
                            .size(16.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.error
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onError,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // Notification text
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Thông báo",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (unreadCount > 0) {
                        "Bạn có $unreadCount thông báo chưa đọc"
                    } else {
                        "Không có thông báo mới"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (unreadCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Arrow or action icon
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
