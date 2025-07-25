package lamdx4.uis.ptithcm.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

/**
 * PTIT Typography Usage Guidelines - MOBILE OPTIMIZED
 *
 * Tối ưu cho màn hình Android nhỏ, text compact hơn web
 * Ưu tiên tiết kiệm không gian và dễ đọc trên mobile
 */
object PTITTypography {

    /**
     * 🏷️ SCREEN HEADERS - Tiêu đề màn hình chính
     * Usage: TopAppBar titles, screen headers
     * Mobile: Nhỏ gọn hơn, không chiếm quá nhiều space
     */
    val screenTitle: TextStyle
        @Composable get() = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Medium
        )

    /**
     * 📄 SECTION HEADERS - Tiêu đề phần, nhóm nội dung
     * Usage: Card headers, section dividers
     * Mobile: Vừa đủ để phân biệt nhưng không quá lớn
     */
    val sectionTitle: TextStyle
        @Composable get() = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Medium
        )

    /**
     * 📝 CARD TITLES - Tiêu đề card, item trong list
     * Usage: Semester cards, subject cards, grade items
     * Mobile: Compact size cho list items
     */
    val cardTitle: TextStyle
        @Composable get() = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Medium
        )

    /**
     * 📄 MAIN CONTENT - Nội dung chính, thông tin quan trọng
     * Usage: Student info, schedule details, descriptions
     * Mobile: Standard body size, dễ đọc nhưng không chiếm nhiều chỗ
     */
    val bodyContent: TextStyle
        @Composable get() = MaterialTheme.typography.bodyMedium

    /**
     * 📝 SECONDARY CONTENT - Thông tin phụ, chi tiết bổ sung
     * Usage: Timestamps, metadata, additional info
     * Mobile: Nhỏ hơn để tiết kiệm space
     */
    val bodySecondary: TextStyle
        @Composable get() = MaterialTheme.typography.bodySmall

    /**
     * 🏷️ SMALL INFO - Thông tin nhỏ, labels
     * Usage: Status badges, small labels, hints
     * Mobile: Rất nhỏ gọn cho labels
     */
    val caption: TextStyle
        @Composable get() = MaterialTheme.typography.labelSmall

    /**
     * 🔘 BUTTONS - Text cho buttons, actions
     * Usage: Button text, tab labels, action items
     * Mobile: Standard button size, touch-friendly
     */
    val buttonText: TextStyle
        @Composable get() = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Medium
        )

    /**
     * ⚠️ ERROR TEXT - Text báo lỗi
     * Usage: Error messages, validation feedback
     * Mobile: Đủ rõ để đọc lỗi nhưng không quá lớn
     */
    val errorText: TextStyle
        @Composable get() = MaterialTheme.typography.bodySmall.copy(
            fontWeight = FontWeight.Medium
        )

    /**
     * ✅ SUCCESS TEXT - Text thông báo thành công
     * Usage: Success messages, confirmation text
     * Mobile: Tương tự error text
     */
    val successText: TextStyle
        @Composable get() = MaterialTheme.typography.bodySmall.copy(
            fontWeight = FontWeight.Medium
        )

    /**
     * 🔢 NUMERIC DISPLAY - Hiển thị số liệu quan trọng
     * Usage: GPA, grades, statistics
     * Mobile: Lớn vừa đủ để highlight số liệu quan trọng
     */
    val numericDisplay: TextStyle
        @Composable get() = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Bold
        )

    /**
     * 🏷️ BADGE TEXT - Text trong badges, chips
     * Usage: Status badges, category chips
     * Mobile: Rất compact cho badges nhỏ
     */
    val badgeText: TextStyle
        @Composable get() = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Medium
        )

    /**
     * 📱 LIST ITEM - Text cho items trong list
     * Usage: Subject names, schedule items
     * Mobile: Tối ưu cho danh sách dài trên mobile
     */
    val listItem: TextStyle
        @Composable get() = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Normal
        )

    /**
     * 🔍 SUBTITLE - Subtitle nhỏ gọn
     * Usage: Descriptions, subtitles under main content
     * Mobile: Nhỏ hơn body content
     */
    val subtitle: TextStyle
        @Composable get() = MaterialTheme.typography.bodySmall.copy(
            fontWeight = FontWeight.Normal
        )
}