package lamdx4.uis.ptithcm.ui.exam

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import lamdx4.uis.ptithcm.service.AlarmForegroundService

class RingingAlarmActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = "Alarm",
                            tint = Color.Red,
                            modifier = Modifier.size(120.dp)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "Báo thức đang reo!",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                stopService(
                                    Intent(
                                        this@RingingAlarmActivity,
                                        AlarmForegroundService::class.java
                                    )
                                )
                                finish()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Tắt báo thức", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
