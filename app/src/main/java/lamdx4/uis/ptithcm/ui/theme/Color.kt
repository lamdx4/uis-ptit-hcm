package lamdx4.uis.ptithcm.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

// Base Red Colors - Logo inspired (Cải tiến)
val PTITRed = Color(0xFFD32F2F)           // Main brand red - chỉ dùng cho accent
val PTITRedVariant = Color(0xFFB71C1C)    // Darker red - cho hover states
val PTITRedLight = Color(0xFFFFEBEE)      // Very light red - cho backgrounds
val PTITRedAccent = Color(0xFFFF5252)     // Bright red - cho notifications

// Neutral Colors - Màu chính cho UI
val PrimaryGray = Color(0xFF37474F)       // Xám xanh chuyên nghiệp
val SecondaryGray = Color(0xFF546E7A)     // Xám nhẹ hơn
val BackgroundGray = Color(0xFFFAFAFA)    // Background rất nhẹ
val SurfaceGray = Color(0xFFFFFFFF)       // Surface trắng
val TextPrimary = Color(0xFF212121)       // Text chính
val TextSecondary = Color(0xFF757575)     // Text phụ

// Supporting Colors
val Success = Color(0xFF4CAF50)
val Warning = Color(0xFFFF9800)
val Info = Color(0xFF2196F3)
val Error = PTITRed                       // Sử dụng brand red cho error

// Light Theme - Cân bằng màu đỏ
val LightColors = lightColorScheme(
    // Primary - Sử dụng xám làm chính, đỏ làm accent
    primary = PrimaryGray,                // Xám làm màu chính
    onPrimary = Color.White,
    primaryContainer = BackgroundGray,
    onPrimaryContainer = TextPrimary,
    
    // Secondary - Màu đỏ brand chỉ dùng cho secondary (ít hơn)
    secondary = PTITRed,                  // Đỏ brand cho secondary
    onSecondary = Color.White,
    secondaryContainer = PTITRedLight,
    onSecondaryContainer = PTITRedVariant,
    
    // Tertiary - Màu xám phụ
    tertiary = SecondaryGray,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFECEFF1),
    onTertiaryContainer = PrimaryGray,
    
    // Background & Surface
    background = BackgroundGray,
    onBackground = TextPrimary,
    surface = SurfaceGray,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = TextSecondary,
    
    // Error - Sử dụng màu đỏ brand
    error = PTITRed,
    onError = Color.White,
    errorContainer = PTITRedLight,
    onErrorContainer = PTITRedVariant,
    
    // Outline
    outline = Color(0xFFBDBDBD),
    outlineVariant = Color(0xFFE0E0E0),
    scrim = Color.Black.copy(alpha = 0.32f)
)

// Dark Theme
val DarkColors = darkColorScheme(
    primary = Color(0xFF90A4AE),          // Xám nhẹ cho dark mode
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF37474F),
    onPrimaryContainer = Color(0xFFCFD8DC),
    
    secondary = PTITRedAccent,            // Đỏ sáng hơn cho dark mode
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF5D1A1A),
    onSecondaryContainer = PTITRedLight,
    
    tertiary = Color(0xFF78909C),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF455A64),
    onTertiaryContainer = Color(0xFFB0BEC5),
    
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color(0xFFBDBDBD),
    
    error = PTITRedAccent,
    onError = Color.Black,
    errorContainer = Color(0xFF5D1A1A),
    onErrorContainer = PTITRedLight,
    
    outline = Color(0xFF757575),
    outlineVariant = Color(0xFF424242),
    scrim = Color.Black.copy(alpha = 0.32f)
)
