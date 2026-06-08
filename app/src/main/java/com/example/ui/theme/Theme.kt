package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    secondary = SecondaryDark,
    tertiary = TertiaryDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = BackgroundDark,
    onSecondary = BackgroundDark,
    onTertiary = BackgroundDark,
    onBackground = Color(0xFFF1F5F9), // slate-100
    onSurface = Color(0xFFF1F5F9), // slate-100
    primaryContainer = Color(0xFF064E3B), // emerald-950/900
    onPrimaryContainer = Color(0xFFD1FAE5), // emerald-100
    secondaryContainer = Color(0xFF7C2D12), // orange-950/900
    onSecondaryContainer = Color(0xFFFFEDD5), // orange-100
    surfaceVariant = Color(0xFF334155), // slate-700
    onSurfaceVariant = Color(0xFFCBD5E1), // slate-300
    outline = OutlineDark,
    outlineVariant = Color(0xFF475569) // slate-600
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    secondary = SecondaryLight,
    tertiary = TertiaryLight,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = SurfaceLight,
    onSecondary = SurfaceLight,
    onTertiary = SurfaceLight,
    onBackground = BackgroundDark,
    onSurface = BackgroundDark,
    primaryContainer = Color(0xFFECFDF5), // emerald-50
    onPrimaryContainer = Color(0xFF064E3B), // emerald-900
    secondaryContainer = Color(0xFFFFF7ED), // orange-50
    onSecondaryContainer = Color(0xFF7C2D12), // orange-900
    surfaceVariant = Color(0xFFF1F5F9), // slate-100
    onSurfaceVariant = Color(0xFF334155), // slate-700
    outline = OutlineLight,
    outlineVariant = Color(0xFFE2E8F0) // slate-200
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set dynamic color is false by default so our custom design theme shines!
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
