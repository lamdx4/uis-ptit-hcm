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
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import lamdx4.uis.ptithcm.data.repository.ScheduleRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ScheduleWidget() : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Lấy repository qua EntryPoint
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            ScheduleRepositoryEntryPoint::class.java
        )
        val scheduleRepository = entryPoint.getScheduleRepository()

        // Lấy lịch tuần hiện tại
        val semesterCode = scheduleRepository.getCurrentSemester()?.semesterCode
            ?: scheduleRepository.getSemesters().data.semesters.first().semesterCode
        val weeklySchedule = scheduleRepository.getCurrentWeek(semesterCode)

        // Lấy ngày hiện tại
        val today = LocalDate.now()
        val showedDate = today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val compareFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val formattedDate = today.atStartOfDay().format(compareFormat)

        val morningClass =
            weeklySchedule?.scheduleItems?.find { it.studyDate == formattedDate && it.startPeriod == 1 }
        val afternoonClass =
            weeklySchedule?.scheduleItems?.find { it.studyDate == formattedDate && it.startPeriod == 7 }

        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(Color(0xFFFFFFFF)) // nền trắng tổng thể
                    .padding(12.dp)
            ) {
                // Hàng đầu: ngày + nút refresh
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = showedDate,
                        style = TextStyle(
                            fontSize = 16.sp
                        ),
                        modifier = GlanceModifier.defaultWeight()
                    )

                    Button(
                        text = "⟳",
                        onClick = { /* TODO: refresh sau */ }
                    )
                }

                // Cột chứa buổi sáng và buổi chiều
                Column(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFEBEE)) // nền đỏ nhạt
                        .padding(8.dp)
                ) {
                    // Buổi sáng
                    Row(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFFFFF)) // ô trắng
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Sáng: ${morningClass?.subjectCode ?: "Rãnh rỗi"}" ,
                            style = TextStyle(
                                fontSize = 14.sp
                            )
                        )
                    }

                    Spacer(modifier = GlanceModifier.height(6.dp))

                    // Buổi chiều
                    Row(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFFFFF)) // ô trắng
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Chiều: ${afternoonClass?.subjectCode ?: "Rãnh rỗi"}",
                            style = TextStyle(
                                fontSize = 14.sp
                            )
                        )
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