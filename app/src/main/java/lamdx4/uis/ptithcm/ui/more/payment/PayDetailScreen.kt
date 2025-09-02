package lamdx4.uis.ptithcm.ui.more.payment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import lamdx4.uis.ptithcm.ui.theme.PTITTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayDetailScreen(
    modifier: Modifier = Modifier,
    nav: NavHostController,
    viewModel: PaymentViewModel = hiltViewModel(),
) {
    var captchaInput by remember { mutableStateOf("") }
    val formState = viewModel.formState.collectAsState()
    val studentData = viewModel.studentPaymentInfo.collectAsState()



}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "$label:",
            style = PTITTypography.bodyContent.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier
                .widthIn(min = 110.dp)
                .weight(0.4f)
        )
        Text(
            text = value,
            style = PTITTypography.bodyContent,
            modifier = Modifier.weight(0.6f)
        )
    }
}