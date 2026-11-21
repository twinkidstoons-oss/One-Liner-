package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val OneLineDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = NavyDeepest,
    primaryContainer = NavySurfaceElevated,
    onPrimaryContainer = NeonCyanSoft,
    secondary = NeonViolet,
    onSecondary = TextPrimary,
    secondaryContainer = NavyMid,
    onSecondaryContainer = TextPrimary,
    tertiary = NeonEmerald,
    onTertiary = NavyDeepest,
    background = NavyBackground,
    onBackground = TextPrimary,
    surface = NavySurface,
    onSurface = TextPrimary,
    surfaceVariant = NavySurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = NavyCardBorder,
    error = NeonCoral,
    onError = TextPrimary,
)

val OneLineShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun OneLineTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = OneLineDarkColorScheme,
        typography = OneLineTypography,
        shapes = OneLineShapes,
        content = content,
    )
}
