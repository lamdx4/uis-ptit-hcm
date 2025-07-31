package lamdx4.uis.ptithcm.ui.login

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.common.activityViewModel
import lamdx4.uis.ptithcm.data.repository.AuthRepository
import lamdx4.uis.ptithcm.ui.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    appViewModel: AppViewModel = activityViewModel(),
    innerPadding: PaddingValues = PaddingValues(),
    onLoginSuccess: () -> Unit
) {
    val userState by appViewModel.uiState.collectAsState()
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var rememberMe by rememberSaveable { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var hasInitialized by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val scrollState = rememberScrollState()

    // Populate from rememberMe ONLY on first load
    LaunchedEffect(userState.rememberMe) {
        if (!hasInitialized && userState.rememberMe && userState.username != null && userState.password != null) {
            username = userState.username.orEmpty()
            password = userState.password.orEmpty()
            rememberMe = true
            hasInitialized = true
        } else if (!hasInitialized) {
            hasInitialized = true
        }
    }

    // Clear profile when username changes (different account)
    LaunchedEffect(username) {
        val currentMaSV = userState.maSV
        if (currentMaSV != null && currentMaSV != username && username.isNotEmpty()) {
            appViewModel.clearProfile()
            // Also clear saved credentials if switching to different account
            if (userState.username != null && userState.username != username) {
                appViewModel.clearLoginInfo()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        // Background Container - Placeholder for background image
        BackgroundContainer()

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo and Header Section
            LogoSection()

            Spacer(modifier = Modifier.height(21.dp))

            // Login Form Card
            LoginFormCard(
                username = username,
                password = password,
                passwordVisible = passwordVisible,
                rememberMe = rememberMe,
                loading = loading,
                error = error,
                onUsernameChange = {
                    username = it
                    error = null // Clear error when user types
                },
                onPasswordChange = {
                    password = it
                    error = null // Clear error when user types
                },
                onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
                onRememberMeChange = { rememberMe = it },
                onLoginClick = {
                    // Clear error and validate inputs
                    error = null

                    if (username.trim().isEmpty()) {
                        error = "Vui lòng nhập tên đăng nhập"
                        return@LoginFormCard
                    }

                    if (password.trim().isEmpty()) {
                        error = "Vui lòng nhập mật khẩu"
                        return@LoginFormCard
                    }

                    loading = true
                    coroutineScope.launch {
                        // Use the current input values, not cached ones
                        val currentUsername = username.trim()
                        val currentPassword = password.trim()

                        val result = authRepository.login(currentUsername, currentPassword)
                        if (result.isSuccess) {
                            val accessToken = result.getOrNull()?.accessToken ?: ""
                            val refreshToken = result.getOrNull()?.refreshToken ?: ""
                            appViewModel.saveLoginInfo(
                                accessToken = accessToken,
                                refreshToken = refreshToken,
                                maSV = currentUsername,
                                username = currentUsername,
                                password = currentPassword,
                                rememberMe = rememberMe
                            )
                            loading = false
                            onLoginSuccess()
                        } else {
                            error =
                                result.exceptionOrNull()?.message ?: "Sai tài khoản hoặc mật khẩu"
                            loading = false
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(21.dp))

            // Footer Section
            FooterSection()
        }
    }
}

@Composable
private fun BackgroundContainer() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Modern gradient background với màu đỏ chủ đạo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        )

        // Optional: Uncomment when you have background image
        /*
        Image(
            painter = painterResource(R.drawable.login_background), // Add your background image here
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.1f
        )
        */

        // Subtle overlay for better readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
                )
        )
    }
}

@Composable
private fun LogoSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Logo Container với design mới
        Card(
            modifier = Modifier.size(140.dp),
            shape = RoundedCornerShape(35.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Background pattern
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.1f),
                                    Color.Transparent
                                ),
                                radius = 100f
                            )
                        )
                )

                // Placeholder for logo - Replace with actual logo
                Text(
                    text = "PTIT",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                // Optional: Uncomment when you have logo image
                /*
                Image(
                    painter = painterResource(R.drawable.ptit_logo), // Add your logo here
                    contentDescription = "PTIT Logo",
                    modifier = Modifier.size(100.dp),
                    contentScale = ContentScale.Fit
                )
                */
            }
        }

        // Welcome Text với typography mới
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Chào mừng trở lại",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Đăng nhập vào hệ thống PTIT",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LoginFormCard(
    username: String,
    password: String,
    passwordVisible: Boolean,
    rememberMe: Boolean,
    loading: Boolean,
    error: String?,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onRememberMeChange: (Boolean) -> Unit,
    onLoginClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Form Header
            Text(
                text = "Đăng nhập",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Username Field với design mới
            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                label = { Text("Tài khoản") },
                placeholder = { Text("Nhập tài khoản của bạn") },
                leadingIcon = {
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Username",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            // Password Field với design mới
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Mật khẩu") },
                placeholder = { Text("Nhập mật khẩu của bạn") },
                leadingIcon = {
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Password",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                trailingIcon = {
                    IconButton(onClick = onPasswordVisibilityToggle) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            // Remember Me Checkbox với design mới
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = onRememberMeChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        checkmarkColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                Text(
                    text = "Lưu thông tin đăng nhập",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Light
                )
            }

            // Error Message với design mới
            error?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "Đăng nhập thất bại",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            // Login Button với design mới
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !loading && username.isNotBlank() && password.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp,
                    disabledElevation = 0.dp
                )
            ) {
                if (loading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Đang đăng nhập...",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Login,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Đăng nhập",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FooterSection() {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Forgot Password Button
        TextButton(
            onClick = { /* Handle forgot password */ },
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Quên mật khẩu?",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        val annotatedText = buildAnnotatedString {
            append("Bằng việc tiếp tục, bạn đồng ý với ")

            // Gắn tag "TERMS" để xử lý click
            pushStringAnnotation(tag = "TERMS", annotation = "https://lamdx4.github.io/uis-ptit-hcm/term")
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("Điều khoản sử dụng")
            }
            pop()
        }

        ClickableText(
            text = annotatedText,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth(),
            onClick = { offset ->
                annotatedText.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                    .firstOrNull()?.let { annotation ->
                        val url = annotation.item
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        context.startActivity(intent, null)
                    }
            }
        )
    }
}
