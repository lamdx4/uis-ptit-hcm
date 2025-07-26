package lamdx4.uis.ptithcm.ui.more

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.common.activityViewModel
import lamdx4.uis.ptithcm.data.model.CompleteStudentInfo
import lamdx4.uis.ptithcm.ui.AppViewModel
import lamdx4.uis.ptithcm.ui.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedInfoScreen(
    navController: NavController,
    appViewModel: AppViewModel = activityViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    // Lấy thông tin student từ AppViewModel
    val userState by appViewModel.uiState.collectAsState()
    val maSV = userState.maSV
    val profile = userState.profile
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Load profile info if not loaded
    LaunchedEffect(maSV) {
        if (profile == null && !isLoading && !maSV.isNullOrEmpty()) {
            isLoading = true
            errorMessage = null
            try {
                val result = profileViewModel.loadProfile(maSV)
                result?.let { appViewModel.setProfile(it) }
            } catch (e: Exception) {
                errorMessage = "Không thể tải thông tin: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Thông tin chi tiết",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            profile != null -> {
                TabbedInformationContent(
                    profile = profile,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Đang tải thông tin chi tiết...")
                    }
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            "Có lỗi xảy ra",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            errorMessage!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(onClick = { navController.navigateUp() }) {
                            Text("Quay lại")
                        }
                    }
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.PersonOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            "Chưa đăng nhập",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Vui lòng đăng nhập để xem thông tin chi tiết",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(onClick = { navController.navigateUp() }) {
                            Text("Quay lại")
                        }
                    }
                }
            }
        }
    }
}

// Actual implementation with real data
@Composable
private fun TabbedInformationContent(
    profile: CompleteStudentInfo,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {
        // Tab Row
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.surface,
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
                        text = "Thông tin học tập",
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

@Composable
private fun PersonalInfoContent(profile: CompleteStudentInfo) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
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

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
    }
}

@Composable
private fun AcademicInfoContent(profile: CompleteStudentInfo) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            val academicInfoItems = listOf(
                Triple("Lớp", profile.lop, Icons.Default.Class),
                Triple("Bậc đào tạo", profile.bac_he_dao_tao, Icons.Default.Grade),
                Triple("Chuyên ngành", profile.chuyen_nganh, Icons.Default.Science),
                Triple("Niên khóa", profile.nien_khoa, Icons.Default.CalendarMonth)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
