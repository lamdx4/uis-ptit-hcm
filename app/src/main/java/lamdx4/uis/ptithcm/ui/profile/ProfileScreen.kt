package lamdx4.uis.ptithcm.ui.profile

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProfileScreen(
    accessToken: String,
    maSV: String,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Chỉ load 1 lần
    LaunchedEffect(Unit) {
        viewModel.loadStudentInfo(accessToken, maSV)
    }

    when {
        uiState.loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        uiState.error != null -> {
            Text("Lỗi: ${uiState.error}")
        }
        uiState.studentInfo != null -> {
            Column(
                Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Top
            ) {
                val imageBase64 = uiState.studentInfo!!.image
                if (!imageBase64.isNullOrEmpty()) {
                    val imageBytes = Base64.decode(imageBase64, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Ảnh sinh viên",
                            modifier = Modifier.size(120.dp)
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Mã SV: ${uiState.studentInfo!!.id_sinh_vien}")
                // ... Hiển thị các trường thông tin khác
            }
        }
    }
}