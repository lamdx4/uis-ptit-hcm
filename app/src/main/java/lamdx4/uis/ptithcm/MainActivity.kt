package lamdx4.uis.ptithcm

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import lamdx4.uis.ptithcm.ui.AppNavHost
import lamdx4.uis.ptithcm.ui.AppViewModel
import lamdx4.uis.ptithcm.ui.theme.PTITTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            PTITTheme {
                AppNavHost(hiltViewModel<AppViewModel>())
            }
        }
    }
}