package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// MIMI V13 Core Design System Colors
val DarkNavyBg = Color(0xFF0A0E1A)
val CardBg = Color(0xFF162032)
val BottomNavBg = Color(0xFF0F172A)
val MimiChatBg = Color(0xFF1E293B)
val BorderGlowBlue = Color(0xFF3B82F6)
val PrimaryBlue = Color(0xFF3B82F6)
val PrimaryRed = Color(0xFFEF4444)
val NeonGreen = Color(0xFF10B981)
val LockedGray = Color(0xFF64748B)
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val AccentYellow = Color(0xFFF59E0B)
val CyanGlow = Color(0xFF06B6D4)

// Signature Blue to Red Cyber Gradient
val CyberGradient = Brush.horizontalGradient(
  colors = listOf(PrimaryBlue, PrimaryRed)
)

val CyberVerticalGradient = Brush.verticalGradient(
  colors = listOf(PrimaryBlue, PrimaryRed)
)

val CardGlowBorderBrush = Brush.horizontalGradient(
  colors = listOf(PrimaryBlue.copy(alpha = 0.6f), PrimaryRed.copy(alpha = 0.4f), PrimaryBlue.copy(alpha = 0.6f))
)
