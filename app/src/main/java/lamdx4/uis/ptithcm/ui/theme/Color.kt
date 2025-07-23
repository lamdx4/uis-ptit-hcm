package lamdx4.uis.ptithcm.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

// === PTIT RED PALETTE - Màu đỏ chủ đạo ===
val PTITRedPrimary = Color(0xFFDC2626)        // Đỏ chính - không quá chói
val PTITRedDark = Color(0xFFB91C1C)           // Đỏ đậm cho pressed states
val PTITRedLight = Color(0xFFEF4444)          // Đỏ sáng cho hover
val PTITRedSoft = Color(0xFFFEF2F2)           // Đỏ rất nhẹ cho backgrounds
val PTITRedContainer = Color(0xFFFFE4E6)      // Container màu đỏ nhẹ

// === WARM NEUTRALS - Màu trung tính ấm để cân bằng đỏ ===
val WarmGray50 = Color(0xFFFAFAF9)            // Background chính
val WarmGray100 = Color(0xFFF5F5F4)           // Surface nhẹ
val WarmGray200 = Color(0xFFE7E5E4)           // Border nhẹ
val WarmGray300 = Color(0xFFD6D3D1)           // Border
val WarmGray400 = Color(0xFFA8A29E)           // Text phụ
val WarmGray500 = Color(0xFF78716C)           // Text secondary
val WarmGray600 = Color(0xFF57534E)           // Text primary
val WarmGray700 = Color(0xFF44403C)           // Text đậm
val WarmGray800 = Color(0xFF292524)           // Text rất đậm
val WarmGray900 = Color(0xFF1C1917)           // Text đen

// === ACCENT COLORS - Màu bổ trợ ===
val BlueAccent = Color(0xFF3B82F6)            // Xanh dương cho info
val BlueAccentLight = Color(0xFFDBEAFE)       // Container xanh nhẹ
val GreenSuccess = Color(0xFF10B981)          // Xanh lá cho success
val GreenSuccessLight = Color(0xFFD1FAE5)     // Container xanh lá nhẹ
val AmberWarning = Color(0xFFF59E0B)          // Vàng cam cho warning
val AmberWarningLight = Color(0xFFFEF3C7)     // Container vàng nhẹ

// === LIGHT THEME - Màu đỏ chủ đạo ===
val LightColors = lightColorScheme(
    // === PRIMARY - Màu đỏ chính ===
    primary = PTITRedPrimary,                 // Đỏ chính
    onPrimary = Color.White,                  // Text trên nền đỏ
    primaryContainer = PTITRedContainer,      // Container đỏ nhẹ
    onPrimaryContainer = PTITRedDark,         // Text trên container đỏ

    // === SECONDARY - Xám ấm để cân bằng ===
    secondary = WarmGray600,                  // Xám ấm
    onSecondary = Color.White,
    secondaryContainer = WarmGray100,         // Container xám nhẹ
    onSecondaryContainer = WarmGray700,

    // === TERTIARY - Xanh dương accent ===
    tertiary = BlueAccent,                    // Xanh dương bổ trợ
    onTertiary = Color.White,
    tertiaryContainer = BlueAccentLight,
    onTertiaryContainer = Color(0xFF1E40AF),

    // === BACKGROUND & SURFACE ===
    background = WarmGray50,                  // Background ấm, không trắng thuần
    onBackground = WarmGray800,               // Text chính
    surface = Color.White,                    // Surface trắng
    onSurface = WarmGray800,                  // Text trên surface
    surfaceVariant = WarmGray100,             // Surface variant
    onSurfaceVariant = WarmGray500,           // Text phụ

    // === OUTLINE ===
    outline = WarmGray300,                    // Border chính
    outlineVariant = WarmGray200,             // Border nhẹ

    // === ERROR ===
    error = PTITRedPrimary,                   // Sử dụng màu đỏ brand cho error
    onError = Color.White,
    errorContainer = PTITRedSoft,
    onErrorContainer = PTITRedDark,

    // === SURFACE TINT & SCRIM ===
    surfaceTint = PTITRedPrimary,
    scrim = Color.Black.copy(alpha = 0.32f),

    // === INVERSE COLORS ===
    inverseSurface = WarmGray800,
//    onInverseSurface = WarmGray100,
    inversePrimary = Color(0xFFFF6B6B)        // Đỏ sáng cho dark surface
)

// === DARK THEME - Điều chỉnh cho dark mode ===
val DarkColors = darkColorScheme(
    // === PRIMARY - Đỏ nhẹ hơn cho dark mode ===
    primary = Color(0xFFFF6B6B),             // Đỏ sáng hơn cho dark
    onPrimary = WarmGray900,                  // Text tối trên nền đỏ sáng
    primaryContainer = Color(0xFF7F1D1D),     // Container đỏ tối
    onPrimaryContainer = Color(0xFFFFCDD2),   // Text sáng trên container tối

    // === SECONDARY ===
    secondary = WarmGray400,                  // Xám sáng cho dark mode
    onSecondary = WarmGray900,
    secondaryContainer = WarmGray700,
    onSecondaryContainer = WarmGray200,

    // === TERTIARY ===
    tertiary = Color(0xFF60A5FA),            // Xanh sáng hơn cho dark
    onTertiary = WarmGray900,
    tertiaryContainer = Color(0xFF1E3A8A),
    onTertiaryContainer = Color(0xFFDBEAFE),

    // === BACKGROUND & SURFACE ===
    background = Color(0xFF0C0A09),           // Background rất tối với hint ấm
    onBackground = WarmGray200,
    surface = Color(0xFF1C1917),             // Surface tối ấm
    onSurface = WarmGray200,
    surfaceVariant = Color(0xFF292524),
    onSurfaceVariant = WarmGray400,

    // === OUTLINE ===
    outline = WarmGray600,
    outlineVariant = WarmGray700,

    // === ERROR ===
    error = Color(0xFFFF6B6B),               // Đỏ sáng cho error trong dark
    onError = WarmGray900,
    errorContainer = Color(0xFF5D1A1A),
    onErrorContainer = Color(0xFFFFCDD2),

    // === SURFACE TINT & SCRIM ===
    surfaceTint = Color(0xFFFF6B6B),
    scrim = Color.Black.copy(alpha = 0.5f),

    // === INVERSE COLORS ===
    inverseSurface = WarmGray100,
//    onInverseSurface = WarmGray800,
    inversePrimary = PTITRedPrimary
)

// === CUSTOM SEMANTIC COLORS - Để sử dụng trong app ===
object PTITColors {
    // Success states
    val success = GreenSuccess
    val onSuccess = Color.White
    val successContainer = GreenSuccessLight
    val onSuccessContainer = Color(0xFF065F46)

    // Warning states
    val warning = AmberWarning
    val onWarning = Color.White
    val warningContainer = AmberWarningLight
    val onWarningContainer = Color(0xFF92400E)

    // Info states
    val info = BlueAccent
    val onInfo = Color.White
    val infoContainer = BlueAccentLight
    val onInfoContainer = Color(0xFF1E40AF)

    // Red variations for different use cases
    val redSubtle = PTITRedSoft           // Cho backgrounds nhẹ
    val redMuted = Color(0xFFFECDD3)      // Cho borders nhẹ
    val redDefault = PTITRedPrimary       // Màu chính
    val redEmphasis = PTITRedDark         // Cho emphasis

    // Neutral variations
    val neutralSubtle = WarmGray50
    val neutralMuted = WarmGray100
    val neutralDefault = WarmGray500
    val neutralEmphasis = WarmGray700
}
