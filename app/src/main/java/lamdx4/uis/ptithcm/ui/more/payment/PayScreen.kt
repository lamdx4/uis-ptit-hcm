package lamdx4.uis.ptithcm.ui.more.payment

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import lamdx4.uis.ptithcm.common.activityViewModel
import lamdx4.uis.ptithcm.ui.AppViewModel
import lamdx4.uis.ptithcm.ui.theme.PTITTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    modifier: Modifier = Modifier,
    viewModel: PaymentViewModel = hiltViewModel<PaymentViewModel>(),
    appViewModel: AppViewModel = activityViewModel()
) {
    var captchaInput by remember { mutableStateOf("") }
    var messageColor by remember { mutableStateOf(Color.Unspecified) }

    val uiState by appViewModel.uiState.collectAsState()
    val studentId = uiState.maSV.orEmpty()
    val formState = viewModel.formState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchForm()
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Thông tin sinh viên",
                    style = PTITTypography.screenTitle,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                if (formState.value != null) {
                    // Input fields and Captcha
                    OutlinedTextField(
                        enabled = false,
                        value = studentId,
                        onValueChange = { },
                        label = { Text("Mã sinh viên (*)", style = PTITTypography.bodyContent) },
                        placeholder = { Text("", style = PTITTypography.bodyContent) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = PTITTypography.bodyContent
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = captchaInput,
                        onValueChange = { captchaInput = it },
                        label = { Text("Mã xác nhận (*)", style = PTITTypography.bodyContent) },
                        placeholder = { Text("", style = PTITTypography.bodyContent) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = PTITTypography.bodyContent
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    formState.value?.let { form ->
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = form.imgCaptchaUrl,
                            ),
                            contentDescription = "Mã xác nhận",
                            modifier = Modifier
                                .width(180.dp)
                                .height(60.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            viewModel.check(studentId, captchaInput)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Thanh toán", style = PTITTypography.buttonText)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Message display
                    formState.value?.message?.let { message ->
                        if (message.isNotEmpty()) {
                            Text(
                                text = message,
                                color = messageColor,
                                style = PTITTypography.bodyContent,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    Text(
                        text = "Các thắc mắc trong việc chuyển khoản kinh phí nhập học thí sinh liên hệ: Số điện thoại liên hệ Phòng KTTC tại cơ sở Thủ Đức: 028.3730 8400 gặp cô Thảo. Sinh viên liên hệ giờ hành chính",
                        style = PTITTypography.bodyContent,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
