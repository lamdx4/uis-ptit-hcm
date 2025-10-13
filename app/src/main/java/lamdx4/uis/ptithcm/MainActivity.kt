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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.ui.AppNavHost
import lamdx4.uis.ptithcm.ui.AppViewModel
import lamdx4.uis.ptithcm.ui.theme.PTITTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private var pendingDest: String? = null

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            setupDynamicShortcuts()
        }

        pendingDest = intent?.getStringExtra("shortcut_destination")

        setContent {
            PTITTheme {
                navController = rememberNavController()
                val appViewModel = hiltViewModel<AppViewModel>()

                var hasNavigated by remember { mutableStateOf(false) }

                AppNavHost(navController, appViewModel)

                LaunchedEffect(navController) {
                    delay(200)
                    if (!hasNavigated && pendingDest != null) {
                        handleShortcutDestination(pendingDest!!)
                        pendingDest = null
                        hasNavigated = true
                    }
                }
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
        dest?.let {
            pendingDest = it
            if (::navController.isInitialized) {
                navController.navigateOrQueue(it)
            }
        }
    }

    private fun NavHostController.navigateOrQueue(route: String) {
        // tránh crash nếu chưa gắn graph
        try {
            navigate(route) {
                launchSingleTop = true
                restoreState = true
            }
        } catch (_: IllegalArgumentException) {
            // Graph chưa attach -> đợi đến khi Activity vào RESUMED rồi điều hướng
            this@MainActivity.lifecycleScope.launch {
                var done = false
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    if (!done) {
                        done = true
                        // chờ 1 nhịp rất ngắn để NavHost hoàn tất setGraph
                        delay(100)
                        this@navigateOrQueue.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }
        }
    }

    private fun handleShortcutDestination(dest: String) {
        val sp = getSharedPreferences("app_prefs", MODE_PRIVATE)
        sp.edit {
            putString("pending_destination", dest)
            putBoolean("pending_from_shortcut", true) // cờ để biết đây là luồng từ shortcut
        }
        val current = navController.currentDestination?.route
        if (current != "login") {
            navController.navigateOrQueue("login")
        }
    }
}
