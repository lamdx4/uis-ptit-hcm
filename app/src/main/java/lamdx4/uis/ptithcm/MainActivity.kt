package lamdx4.uis.ptithcm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import lamdx4.uis.ptithcm.ui.AppNavHost
import lamdx4.uis.ptithcm.ui.theme.PTITTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PTITTheme {
                AppNavHost()
            }
        }
    }
}