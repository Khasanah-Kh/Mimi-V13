package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBg
import com.example.ui.theme.DeepCyberObsidian
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.HudPanelBg
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.NeonCyanMagentaBorderBrush
import com.example.ui.theme.NeonGamerGradient
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.PrimaryRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SignInScreen(
  isLoading: Boolean,
  errorMessage: String?,
  onSignInClick: () -> Unit
) {
  val scrollState = rememberScrollState()
  val infiniteTransition = rememberInfiniteTransition(label = "cyber_gamer_login_transition")

  val neonPulse by infiniteTransition.animateFloat(
    initialValue = 0.55f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "neon_pulse"
  )

  val ringRotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 9000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "ring_rotation"
  )

  val scanlineProgress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 4200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "scanline_progress"
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(DeepCyberObsidian)
      .testTag("cyber_sign_in_screen"),
    contentAlignment = Alignment.Center
  ) {
    // Background Cyber-Gamer Grid + Neon Aura Canvas
    CyberNeonBackdropCanvas(
      neonPulse = neonPulse,
      scanlineProgress = scanlineProgress,
      modifier = Modifier.fillMaxSize()
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.safeDrawing)
        .padding(horizontal = 20.dp)
        .verticalScroll(scrollState),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 500.dp)
          .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top HUD System Telemetry Pill
        CyberHudStatusBar(neonPulse = neonPulse)

        Spacer(modifier = Modifier.height(22.dp))

        // Holographic Neon Avatar Portal
        CyberMascotHoloCore(
          neonPulse = neonPulse,
          ringRotation = ringRotation
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Neon Gamer Title & Subtitle
        Text(
          text = "NEURAL STUDY ARENA",
          fontSize = 11.sp,
          fontWeight = FontWeight.ExtraBold,
          fontFamily = FontFamily.Monospace,
          color = ElectricCyan,
          letterSpacing = 3.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "MIMI CYBER STUDY",
          style = TextStyle(
            brush = NeonGamerGradient,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.6.sp,
            textAlign = TextAlign.Center
          )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Asisten AI Kurikulum Merdeka • Gamifikasi XP & Misi Harian",
          fontSize = 13.sp,
          color = TextPrimary.copy(alpha = 0.85f),
          fontWeight = FontWeight.Medium,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(22.dp))

        // 3 Cyber-Gamer Loadout Perk Cards
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          CyberPerkCard(
            icon = Icons.Default.SportsEsports,
            tag = "QUEST & XP",
            title = "Level Up",
            subtitle = "Streak & Lencana",
            neonColor = ElectricCyan,
            modifier = Modifier.weight(1f)
          )
          CyberPerkCard(
            icon = Icons.Default.Memory,
            tag = "AI TUTOR",
            title = "SD - SMA/K",
            subtitle = "Neural Mentor",
            neonColor = NeonMagenta,
            modifier = Modifier.weight(1f)
          )
          CyberPerkCard(
            icon = Icons.Default.Bolt,
            tag = "CLOUD SAVE",
            title = "Auto Sync",
            subtitle = "Anti-Reset Data",
            neonColor = MatrixGreen,
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main Neon-Framed Firebase Authentication Terminal Card
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clip(CutCornerShape(topStart = 18.dp, bottomEnd = 18.dp, topEnd = 6.dp, bottomStart = 6.dp))
            .border(
              width = 1.5.dp,
              brush = NeonCyanMagentaBorderBrush,
              shape = CutCornerShape(topStart = 18.dp, bottomEnd = 18.dp, topEnd = 6.dp, bottomStart = 6.dp)
            ),
          color = HudPanelBg.copy(alpha = 0.94f)
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Terminal Header Bar
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CardBg.copy(alpha = 0.9f))
                .border(1.dp, ElectricCyan.copy(alpha = 0.28f), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.CloudDone,
                  contentDescription = "Firebase Cloud Link",
                  tint = MatrixGreen,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "FIREBASE AUTH // PLAYER LOGIN",
                  fontSize = 11.sp,
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  color = MatrixGreen,
                  letterSpacing = 0.8.sp
                )
              }

              Text(
                text = "SYNC READY",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                color = ElectricCyan
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = "Hubungkan Google Play / Google Account untuk mengamankan profil pemain, XP, riwayat chat MIMI, dan progres belajarmu di Cloud Firestore.",
              fontSize = 12.sp,
              color = TextSecondary,
              textAlign = TextAlign.Center,
              lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Error Alert Banner
            AnimatedVisibility(
              visible = errorMessage != null,
              enter = fadeIn(),
              exit = fadeOut()
            ) {
              if (errorMessage != null) {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PrimaryRed.copy(alpha = 0.16f))
                    .border(1.dp, NeonMagenta.copy(alpha = 0.65f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
                ) {
                  Text(
                    text = "⚠ AUTH ERROR: $errorMessage",
                    color = Color(0xFFFF80AB),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                  )
                }
              }
            }

            // Neon Cyber-Gamer Google Sign-In CTA Button
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                  brush = Brush.horizontalGradient(
                    colors = listOf(
                      ElectricCyan.copy(alpha = 0.85f * neonPulse),
                      NeonPurple.copy(alpha = 0.9f),
                      NeonMagenta.copy(alpha = 0.85f * neonPulse)
                    )
                  )
                )
                .padding(2.dp)
            ) {
              Button(
                onClick = onSignInClick,
                enabled = !isLoading,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(54.dp)
                  .testTag("google_sign_in_button"),
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color(0xFF091020),
                  contentColor = Color.White,
                  disabledContainerColor = Color(0xFF091020).copy(alpha = 0.7f),
                  disabledContentColor = Color.White.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(14.dp)
              ) {
                if (isLoading) {
                  CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = ElectricCyan,
                    strokeWidth = 2.5.dp
                  )
                  Spacer(modifier = Modifier.width(12.dp))
                  Text(
                    text = "INITIALIZING GOOGLE LINK...",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = ElectricCyan,
                    letterSpacing = 0.8.sp
                  )
                } else {
                  // Neon-backed Google "G" Emblem
                  Box(
                    modifier = Modifier
                      .size(28.dp)
                      .clip(CircleShape)
                      .background(Color.White)
                      .border(1.5.dp, ElectricCyan, CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = "G",
                      color = Color(0xFF2563EB),
                      fontWeight = FontWeight.Black,
                      fontSize = 16.sp
                    )
                  }
                  Spacer(modifier = Modifier.width(12.dp))
                  Column(horizontalAlignment = Alignment.Start) {
                    Text(
                      text = "MASUK DENGAN GOOGLE",
                      fontWeight = FontWeight.Black,
                      fontSize = 14.sp,
                      color = Color.White,
                      letterSpacing = 1.sp
                    )
                    Text(
                      text = "ONE-TAP CREDENTIAL LINK // ZERO-TRUST",
                      fontSize = 9.sp,
                      fontFamily = FontFamily.Monospace,
                      color = ElectricCyan,
                      letterSpacing = 0.5.sp
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-terminal telemetry row
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "PROTOCOL: OAUTH 2.0",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = TextSecondary.copy(alpha = 0.7f)
              )
              Text(
                text = "LATENCY: 12ms • TLS 1.3",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = MatrixGreen.copy(alpha = 0.8f)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Bottom Security Shield Footer
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center,
          modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(CardBg.copy(alpha = 0.6f))
            .border(1.dp, NeonPurple.copy(alpha = 0.3f), RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = "Keamanan",
            tint = NeonPurple,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Terproteksi Firebase Auth & Firestore Security Rules",
            fontSize = 11.sp,
            color = TextPrimary.copy(alpha = 0.75f)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = ElectricCyan,
            modifier = Modifier.size(12.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun CyberHudStatusBar(neonPulse: Float) {
  Row(
    modifier = Modifier
      .clip(RoundedCornerShape(50))
      .background(HudPanelBg.copy(alpha = 0.9f))
      .border(1.dp, ElectricCyan.copy(alpha = 0.45f), RoundedCornerShape(50))
      .padding(horizontal = 14.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(MatrixGreen.copy(alpha = neonPulse))
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = "SYS.ONLINE // MIMI-V13 NEURAL LINK",
      fontSize = 11.sp,
      fontFamily = FontFamily.Monospace,
      fontWeight = FontWeight.Bold,
      color = ElectricCyan,
      letterSpacing = 1.sp
    )
  }
}

@Composable
private fun CyberMascotHoloCore(
  neonPulse: Float,
  ringRotation: Float
) {
  Box(
    modifier = Modifier.size(132.dp),
    contentAlignment = Alignment.Center
  ) {
    // Animated Neon Outer HUD Rings
    Canvas(modifier = Modifier.fillMaxSize()) {
      val centerOffset = Offset(size.width / 2f, size.height / 2f)
      val outerRadius = size.minDimension / 2f - 4.dp.toPx()
      val innerRadius = outerRadius - 10.dp.toPx()

      // Soft radial glow behind the mascot
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            ElectricCyan.copy(alpha = 0.28f * neonPulse),
            NeonMagenta.copy(alpha = 0.16f * neonPulse),
            Color.Transparent
          ),
          center = centerOffset,
          radius = outerRadius * 1.25f
        ),
        radius = outerRadius * 1.25f,
        center = centerOffset
      )

      // Dashed technical telemetry circle
      drawCircle(
        color = NeonPurple.copy(alpha = 0.45f),
        radius = outerRadius,
        center = centerOffset,
        style = Stroke(
          width = 1.5.dp.toPx(),
          pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), phase = ringRotation)
        )
      )

      // Electric Cyan sweeping arc
      drawArc(
        color = ElectricCyan,
        startAngle = ringRotation,
        sweepAngle = 110f,
        useCenter = false,
        topLeft = Offset(centerOffset.x - outerRadius, centerOffset.y - outerRadius),
        size = Size(outerRadius * 2f, outerRadius * 2f),
        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
      )

      // Neon Magenta counter-sweeping arc
      drawArc(
        color = NeonMagenta,
        startAngle = ringRotation + 180f,
        sweepAngle = 110f,
        useCenter = false,
        topLeft = Offset(centerOffset.x - outerRadius, centerOffset.y - outerRadius),
        size = Size(outerRadius * 2f, outerRadius * 2f),
        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
      )

      // Inner glowing ring
      drawCircle(
        brush = Brush.sweepGradient(
          colors = listOf(ElectricCyan, NeonPurple, NeonMagenta, ElectricCyan)
        ),
        radius = innerRadius,
        center = centerOffset,
        style = Stroke(width = 2.dp.toPx())
      )
    }

    // Core Avatar Container
    Box(
      modifier = Modifier
        .size(92.dp)
        .clip(CircleShape)
        .background(
          Brush.radialGradient(
            colors = listOf(
              Color(0xFF172554),
              DeepCyberObsidian
            )
          )
        )
        .border(1.5.dp, ElectricCyan.copy(alpha = 0.7f), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Text(text = "🤖", fontSize = 44.sp)
    }

    // Floating Level Badge at Bottom of Core
    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .clip(RoundedCornerShape(50))
        .background(
          Brush.horizontalGradient(
            colors = listOf(NeonMagenta, NeonPurple)
          )
        )
        .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(50))
        .padding(horizontal = 10.dp, vertical = 2.dp)
    ) {
      Text(
        text = "AI CORE • LVL 99",
        fontSize = 9.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Black,
        color = Color.White,
        letterSpacing = 0.8.sp
      )
    }
  }
}

@Composable
private fun CyberPerkCard(
  icon: ImageVector,
  tag: String,
  title: String,
  subtitle: String,
  neonColor: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .clip(CutCornerShape(topEnd = 12.dp, bottomStart = 12.dp, topStart = 4.dp, bottomEnd = 4.dp))
      .border(
        width = 1.dp,
        color = neonColor.copy(alpha = 0.45f),
        shape = CutCornerShape(topEnd = 12.dp, bottomStart = 12.dp, topStart = 4.dp, bottomEnd = 4.dp)
      ),
    color = HudPanelBg.copy(alpha = 0.88f)
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      horizontalAlignment = Alignment.Start
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = neonColor,
          modifier = Modifier.size(18.dp)
        )
        Text(
          text = tag,
          color = neonColor,
          fontSize = 8.sp,
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.ExtraBold
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = title,
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.ExtraBold
      )
      Text(
        text = subtitle,
        color = TextSecondary,
        fontSize = 10.sp
      )
    }
  }
}

@Composable
private fun CyberNeonBackdropCanvas(
  neonPulse: Float,
  scanlineProgress: Float,
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier) {
    val w = size.width
    val h = size.height

    // Top-left Electric Cyan nebula glow
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          ElectricCyan.copy(alpha = 0.16f * neonPulse),
          Color.Transparent
        ),
        center = Offset(w * 0.18f, h * 0.18f),
        radius = w * 0.65f
      ),
      radius = w * 0.65f,
      center = Offset(w * 0.18f, h * 0.18f)
    )

    // Bottom-right Neon Magenta cyber glow
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          NeonMagenta.copy(alpha = 0.15f * neonPulse),
          NeonPurple.copy(alpha = 0.08f),
          Color.Transparent
        ),
        center = Offset(w * 0.85f, h * 0.82f),
        radius = w * 0.7f
      ),
      radius = w * 0.7f,
      center = Offset(w * 0.85f, h * 0.82f)
    )

    // Subtle background tactical grid lines
    val gridSpacing = 44.dp.toPx()
    var x = 0f
    while (x < w) {
      drawLine(
        color = ElectricCyan.copy(alpha = 0.045f),
        start = Offset(x, 0f),
        end = Offset(x, h),
        strokeWidth = 1f
      )
      x += gridSpacing
    }

    var y = 0f
    while (y < h) {
      drawLine(
        color = NeonPurple.copy(alpha = 0.045f),
        start = Offset(0f, y),
        end = Offset(w, y),
        strokeWidth = 1f
      )
      y += gridSpacing
    }

    // Animated vertical neon scanline sweep
    val scanY = h * scanlineProgress
    drawLine(
      brush = Brush.horizontalGradient(
        colors = listOf(
          Color.Transparent,
          ElectricCyan.copy(alpha = 0.25f),
          NeonMagenta.copy(alpha = 0.25f),
          Color.Transparent
        )
      ),
      start = Offset(0f, scanY),
      end = Offset(w, scanY),
      strokeWidth = 2.dp.toPx()
    )
  }
}
