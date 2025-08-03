package lamdx4.uis.ptithcm.common

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.internal.lifecycle.HiltViewModelFactory


@Composable
inline fun <reified VM : ViewModel> activityViewModel(): VM {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
        ?: error("LocalContext.current không phải là ComponentActivity")

    val factory = HiltViewModelFactory(context, activity.defaultViewModelProviderFactory)

    return viewModel(
        viewModelStoreOwner = activity,
        factory = factory
    )
}

