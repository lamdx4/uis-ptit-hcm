package lamdx4.uis.ptithcm

import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import lamdx4.uis.ptithcm.ui.AppNavHost
import lamdx4.uis.ptithcm.ui.AppViewModel
import lamdx4.uis.ptithcm.ui.theme.PTITTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            setupDynamicShortcuts()
        }

        setContent {
            PTITTheme {
                navController = rememberNavController()

                // Handle intent when turn on the app
                val shortcutDest = intent?.getStringExtra("shortcut_destination")
                LaunchedEffect(shortcutDest) {
                    shortcutDest?.let { handleShortcutDestination(it) }
                }

                AppNavHost(hiltViewModel<AppViewModel>())
            }
        }
    }

    private fun setupDynamicShortcuts() {
        val shortcutManager = getSystemService(ShortcutManager::class.java)

        // Shortcut: Schedule
        val scheduleShortcut = ShortcutInfo.Builder(this, "schedule")
            .setShortLabel("Lịch học")
            .setLongLabel("Xem lịch học")
            .setIcon(Icon.createWithResource(this, R.drawable.outline_date_range_24))
            .setIntent(
                Intent(this, MainActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    putExtra("shortcut_destination", "schedule")
                }
            )
            .build()

        // Shortcut: Exam
        val examShortcut = ShortcutInfo.Builder(this, "exam")
            .setShortLabel("Lịch thi")
            .setLongLabel("Xem lịch thi")
            .setIcon(Icon.createWithResource(this, R.drawable.outline_assignment_24))
            .setIntent(
                Intent(this, MainActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    putExtra("shortcut_destination", "exam")
                }
            )
            .build()

        // Shortcut: Grade
        val gradeShortcut = ShortcutInfo.Builder(this, "grades")
            .setShortLabel("Điểm")
            .setLongLabel("Xem điểm")
            .setIcon(Icon.createWithResource(this, R.drawable.outline_pin_24))
            .setIntent(
                Intent(this, MainActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    putExtra("shortcut_destination", "grades")
                }
            )
            .build()

        shortcutManager?.dynamicShortcuts = listOf(scheduleShortcut, examShortcut, gradeShortcut)
    }

    // Handle intent when app is already open and user selects shortcut
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val dest = intent.getStringExtra("shortcut_destination")
        dest?.let { handleShortcutDestination(it) }
    }

    private fun handleShortcutDestination(dest: String) {
        when (dest) {
            "schedule" -> navController.navigate("schedule") {
                launchSingleTop = true
                restoreState = true
            }

            "exam" -> navController.navigate("exam") {
                launchSingleTop = true
                restoreState = true
            }

            "grades" -> navController.navigate("grades") {
                launchSingleTop = true
                restoreState = true
            }
        }
    }
}
