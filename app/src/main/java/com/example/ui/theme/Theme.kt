package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MimiDarkColorScheme = darkColorScheme(
  primary = PrimaryBlue,
  secondary = PrimaryRed,
  tertiary = NeonGreen,
  background = DarkNavyBg,
  surface = CardBg,
  surfaceVariant = MimiChatBg,
  onPrimary = Color.White,
  onSecondary = Color.White,
  onTertiary = Color.White,
  onBackground = TextPrimary,
  onSurface = TextPrimary,
  onSurfaceVariant = TextSecondary,
  outline = BorderGlowBlue.copy(alpha = 0.5f)
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit
) {
  // Always use the Cyber Gamer Dark Navy theme for MIMI V13
  MaterialTheme(
    colorScheme = MimiDarkColorScheme,
    typography = Typography,
    content = content
  )
}
