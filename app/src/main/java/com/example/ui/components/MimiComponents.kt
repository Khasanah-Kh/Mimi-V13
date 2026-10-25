package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BottomNavTab
import com.example.ui.theme.BottomNavBg
import com.example.ui.theme.CardBg
import com.example.ui.theme.CardGlowBorderBrush
import com.example.ui.theme.CyberGradient
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Custom Glow Card container with #162032 background and neon border
 */
@Composable
fun GlowCard(
  modifier: Modifier = Modifier,
  glowBrush: Brush = CardGlowBorderBrush,
  borderWidth: Dp = 1.5.dp,
  cornerRadius: Dp = 16.dp,
  onClick: (() -> Unit)? = null,
  content: @Composable () -> Unit
) {
  val shape = RoundedCornerShape(cornerRadius)
  val clickableModifier = if (onClick != null) {
    Modifier.clickable(onClick = onClick)
  } else Modifier

  Box(
    modifier = modifier
      .clip(shape)
      .background(CardBg, shape)
      .border(borderWidth, glowBrush, shape)
      .then(clickableModifier)
  ) {
    content()
  }
}

/**
 * Cyber Gradient Button (Blue to Red) with bold text and thumb-friendly touch target
 */
@Composable
fun CyberGradientButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  icon: String? = null,
  testTag: String = "cyber_button"
) {
  val shape = RoundedCornerShape(14.dp)
  val gradient = if (enabled) CyberGradient else Brush.horizontalGradient(
    listOf(Color(0xFF334155), Color(0xFF1E293B))
  )

  Box(
    modifier = modifier
      .height(52.dp)
      .clip(shape)
      .background(gradient, shape)
      .clickable(
        enabled = enabled,
        onClick = onClick
      )
      .padding(horizontal = 20.dp)
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (icon != null) {
        Text(text = icon, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(
        text = text,
        color = if (enabled) Color.White else Color(0xFF94A3B8),
        fontWeight = FontWeight.ExtraBold,
        fontSize = 15.sp,
        letterSpacing = 0.5.sp
      )
    }
  }
}

/**
 * Cyber Progress Bar with Blue to Red gradient fill
 */
@Composable
fun CyberProgressBar(
  progress: Float,
  modifier: Modifier = Modifier,
  height: Dp = 10.dp
) {
  val clamped = progress.coerceIn(0f, 1f)
  val shape = RoundedCornerShape(height / 2)

  Box(
    modifier = modifier
      .height(height)
      .clip(shape)
      .background(Color(0xFF0F172A), shape)
      .border(0.8.dp, PrimaryBlue.copy(alpha = 0.4f), shape)
  ) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
      val fillWidth = maxWidth * clamped
      Box(
        modifier = Modifier
          .fillMaxHeight()
          .width(fillWidth)
          .clip(shape)
          .background(CyberGradient, shape)
      )
    }
  }
}

/**
 * Cute Cyber Fox Mascot (MIMI) with glowing cyber headset and fox ears
 */
@Composable
fun MimiFoxMascot(
  modifier: Modifier = Modifier,
  size: Dp = 56.dp,
  showPulse: Boolean = true
) {
  val infiniteTransition = rememberInfiniteTransition(label = "mimi_pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.98f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "mimi_scale"
  )

  val animatedModifier = if (showPulse) {
    modifier.size(size * pulseScale)
  } else {
    modifier.size(size)
  }

  Box(
    modifier = animatedModifier,
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = this.size.width
      val h = this.size.height

      // Left Fox Ear (Blue Neon)
      val leftEar = Path().apply {
        moveTo(w * 0.32f, h * 0.30f)
        lineTo(w * 0.18f, h * 0.52f)
        lineTo(w * 0.42f, h * 0.46f)
        close()
      }
      drawPath(leftEar, color = PrimaryBlue)

      val leftEarInner = Path().apply {
        moveTo(w * 0.30f, h * 0.35f)
        lineTo(w * 0.23f, h * 0.48f)
        lineTo(w * 0.38f, h * 0.44f)
        close()
      }
      drawPath(leftEarInner, color = PrimaryRed)

      // Right Fox Ear (Red Neon)
      val rightEar = Path().apply {
        moveTo(w * 0.68f, h * 0.30f)
        lineTo(w * 0.58f, h * 0.46f)
        lineTo(w * 0.82f, h * 0.52f)
        close()
      }
      drawPath(rightEar, color = PrimaryRed)

      val rightEarInner = Path().apply {
        moveTo(w * 0.70f, h * 0.35f)
        lineTo(w * 0.62f, h * 0.44f)
        lineTo(w * 0.77f, h * 0.48f)
        close()
      }
      drawPath(rightEarInner, color = PrimaryBlue)

      // Head Base (Dark Cyber Navy)
      val headPath = Path().apply {
        moveTo(w * 0.30f, h * 0.46f)
        cubicTo(w * 0.22f, h * 0.64f, w * 0.32f, h * 0.78f, w * 0.50f, h * 0.88f)
        cubicTo(w * 0.68f, h * 0.78f, w * 0.78f, h * 0.64f, w * 0.70f, h * 0.46f)
        cubicTo(w * 0.60f, h * 0.52f, w * 0.40f, h * 0.52f, w * 0.30f, h * 0.46f)
        close()
      }
      drawPath(headPath, color = CardBg)
      drawPath(headPath, color = PrimaryBlue, style = Stroke(width = 2.5f))

      // Cyber Visor (Blue to Red gradient)
      drawRoundRect(
        brush = Brush.horizontalGradient(listOf(PrimaryBlue, PrimaryRed)),
        topLeft = Offset(w * 0.32f, h * 0.56f),
        size = Size(w * 0.36f, h * 0.14f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
      )

      // Cyber eye slits (Glow white)
      drawRoundRect(
        color = Color.White,
        topLeft = Offset(w * 0.38f, h * 0.60f),
        size = Size(w * 0.08f, h * 0.04f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
      )
      drawRoundRect(
        color = Color.White,
        topLeft = Offset(w * 0.54f, h * 0.60f),
        size = Size(w * 0.08f, h * 0.04f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
      )

      // Nose (Neon Red dot)
      drawCircle(
        color = PrimaryRed,
        radius = w * 0.035f,
        center = Offset(w * 0.50f, h * 0.76f)
      )

      // Cute mouth
      val mouth = Path().apply {
        moveTo(w * 0.45f, h * 0.80f)
        quadraticBezierTo(w * 0.50f, h * 0.83f, w * 0.55f, h * 0.80f)
      }
      drawPath(mouth, color = Color.White, style = Stroke(width = 2f, cap = StrokeCap.Round))

      // Headset band over head
      val headsetBand = Path().apply {
        moveTo(w * 0.22f, h * 0.54f)
        cubicTo(w * 0.20f, h * 0.32f, w * 0.80f, h * 0.32f, w * 0.78f, h * 0.54f)
      }
      drawPath(headsetBand, color = PrimaryBlue, style = Stroke(width = 3.5f))

      // Headset microphone boom
      val mic = Path().apply {
        moveTo(w * 0.24f, h * 0.56f)
        cubicTo(w * 0.24f, h * 0.76f, w * 0.40f, h * 0.82f, w * 0.44f, h * 0.82f)
      }
      drawPath(mic, color = PrimaryRed, style = Stroke(width = 2.5f, cap = StrokeCap.Round))
      drawCircle(
        color = Color(0xFF06B6D4),
        radius = w * 0.035f,
        center = Offset(w * 0.44f, h * 0.82f)
      )
    }
  }
}

/**
 * Fixed Bottom Navigation Bar styled like TikTok:
 * 5 big icons: 🏠 Beranda (blue active), ⚔️ Tantangan, ❓ Kuis, 🏆 Lencana, 👤 Profil
 * Fixed bottom, #0F172A
 */
@Composable
fun CyberBottomBar(
  currentTab: BottomNavTab,
  onTabSelected: (BottomNavTab) -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(BottomNavBg)
      .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
          listOf(PrimaryBlue.copy(alpha = 0.35f), Color.Transparent)
        ),
        shape = RectangleShape
      )
      .windowInsetsPadding(WindowInsets.navigationBars)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(68.dp)
        .padding(horizontal = 8.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      BottomNavTab.values().forEach { tab ->
        val isSelected = tab == currentTab

        Column(
          modifier = Modifier
            .weight(1f)
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
              onClick = { onTabSelected(tab) }
            )
            .testTag("nav_tab_${tab.name.lowercase()}"),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          // Icon with glow when active
          Box(
            contentAlignment = Alignment.Center
          ) {
            if (isSelected) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .background(
                    brush = Brush.radialGradient(
                      listOf(PrimaryBlue.copy(alpha = 0.45f), Color.Transparent)
                    ),
                    shape = CircleShape
                  )
              )
            }
            Text(
              text = tab.emoji,
              fontSize = if (isSelected) 24.sp else 21.sp
            )
          }

          Spacer(modifier = Modifier.height(3.dp))

          // Label
          Text(
            text = tab.title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) PrimaryBlue else TextSecondary
          )

          // Active indicator pill
          if (isSelected) {
            Spacer(modifier = Modifier.height(2.dp))
            Box(
              modifier = Modifier
                .width(16.dp)
                .height(2.dp)
                .clip(CircleShape)
                .background(CyberGradient)
            )
          }
        }
      }
    }
  }
}
