package lamdx4.uis.ptithcm.ui.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import lamdx4.uis.ptithcm.data.repository.ScheduleRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class ScheduleWidget() : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            ScheduleRepositoryEntryPoint::class.java
        )
        val scheduleRepository = entryPoint.getScheduleRepository()

        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", Locale("vi", "VN"))
        val showedDate = today.format(formatter).replaceFirstChar { it.uppercaseChar() }

        val morningClass = scheduleRepository.getClassFromCache(today.toString(), 1)
        val afternoonClass = scheduleRepository.getClassFromCache(today.toString(), 7)

        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(ColorProvider(Color.White, Color(0xFF121212)))
            ) {
                // Header
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .background(
                            ColorProvider(
                                Color(0xFFB71C1C),
                                Color(0xFF333333)
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = showedDate,
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = ColorProvider(Color.White, Color.White)
                        ),
                        modifier = GlanceModifier.defaultWeight()
                    )

                    Button(
                        text = "⟳",
                        onClick = { /* TODO: refresh */ },
                        modifier = GlanceModifier
                            .background(
                                ColorProvider(
                                    Color.White,
                                    Color(0xFFB71C1C)
                                )
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Schedule section
                Column(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .background(
                            ColorProvider(
                                Color(0xFFFFEBEE),
                                Color(0xFF1E1E1E)
                            )
                        )
                        .padding(8.dp)
                ) {
                    // Morning
                    Row(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .background(ColorProvider(Color.White, Color(0xFF2C2C2C)))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Label
                        Text(
                            text = "Sáng",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = ColorProvider(
                                    Color(0xFFB71C1C),
                                    Color(0xFFFFCDD2)
                                ) // đỏ / hồng nhạt
                            ),
                            modifier = GlanceModifier.padding(end = 8.dp)
                        )
                        // Details
                        Column(modifier = GlanceModifier.defaultWeight()) {
                            Text(
                                text = morningClass?.subjectName ?: "Rảnh rỗi",
                                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = morningClass?.subjectCode ?: "",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = ColorProvider(Color.Black, Color.LightGray)
                                )
                            )
                            Text(
                                text = morningClass?.roomCode ?: "",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = ColorProvider(Color.Black, Color.LightGray)
                                )
                            )
                        }
                    }

                    Spacer(modifier = GlanceModifier.height(8.dp))

                    // Afternoon
                    Row(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .background(ColorProvider(Color.White, Color(0xFF2C2C2C)))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Chiều",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = ColorProvider(Color(0xFFB71C1C), Color(0xFFFFCDD2))
                            ),
                            modifier = GlanceModifier.padding(end = 8.dp)
                        )

                        Column(modifier = GlanceModifier.defaultWeight()) {
                            Text(
                                text = afternoonClass?.subjectName ?: "Rảnh rỗi",
                                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = afternoonClass?.subjectCode ?: "",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = ColorProvider(Color.Black, Color.LightGray)
                                )
                            )
                            Text(
                                text = afternoonClass?.roomCode ?: "",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = ColorProvider(Color.Black, Color.LightGray)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ScheduleRepositoryEntryPoint {
    fun getScheduleRepository(): ScheduleRepository
}
