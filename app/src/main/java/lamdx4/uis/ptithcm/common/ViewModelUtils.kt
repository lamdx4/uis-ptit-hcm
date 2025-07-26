package lamdx4.uis.ptithcm.common

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
inline fun <reified VM : ViewModel> activityViewModel(): VM {
    val context = LocalContext.current
    return viewModel(viewModelStoreOwner = context as ComponentActivity)
}