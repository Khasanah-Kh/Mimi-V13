package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBg
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyberGradient
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PrimaryBlue
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

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkNavyBg)
      .padding(horizontal = 24.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(scrollState),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Glow Avatar Header
      Box(
        modifier = Modifier
          .size(104.dp)
          .clip(CircleShape)
          .background(CyberGradient)
          .padding(3.dp),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
            .background(DarkNavyBg),
          contentAlignment = Alignment.Center
        ) {
          Text(text = "🤖", fontSize = 48.sp)
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // App Title
      Text(
        text = "MIMI CYBER STUDY",
        fontSize = 26.sp,
        fontWeight = FontWeight.Black,
        color = Color.White,
        letterSpacing = 1.2.sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Asisten Belajar Kurikulum Merdeka & Teman Curhat Siswa",
        fontSize = 14.sp,
        color = CyanGlow,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Benefit Pills
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FeatureCard(
          icon = Icons.Default.School,
          title = "SD, SMP, SMA/K",
          subtitle = "Materi Lengkap",
          accentColor = CyanGlow,
          modifier = Modifier.weight(1f)
        )
        FeatureCard(
          icon = Icons.Default.Star,
          title = "Gamifikasi XP",
          subtitle = "Streak & Lencana",
          accentColor = PrimaryBlue,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(28.dp))

      // Cloud Sync Login Card
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .border(1.dp, CyanGlow.copy(alpha = 0.35f), RoundedCornerShape(20.dp)),
        color = CardBg.copy(alpha = 0.95f)
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.CloudDone,
              contentDescription = "Cloud Sync",
              tint = NeonGreen,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Cloud Data Persistence",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = NeonGreen
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Masuk dengan akun Google untuk mengamankan data profil, riwayat chat dengan MIMI, XP, dan streak belajarmu di Cloud Firestore.",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
          )

          Spacer(modifier = Modifier.height(20.dp))

          // Error banner
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
                  .background(PrimaryRed.copy(alpha = 0.15f))
                  .border(1.dp, PrimaryRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                  .padding(12.dp)
              ) {
                Text(
                  text = errorMessage,
                  color = PrimaryRed,
                  fontSize = 12.sp,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.fillMaxWidth()
                )
              }
            }
          }

          // Google Sign-In Button
          Button(
            onClick = onSignInClick,
            enabled = !isLoading,
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("google_sign_in_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color.White,
              contentColor = Color(0xFF1F1F1F),
              disabledContainerColor = Color.White.copy(alpha = 0.6f)
            ),
            shape = RoundedCornerShape(26.dp)
          ) {
            if (isLoading) {
              CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color(0xFF1F1F1F),
                strokeWidth = 2.dp
              )
              Spacer(modifier = Modifier.width(12.dp))
              Text(
                text = "Menghubungkan ke Google...",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
              )
            } else {
              // Google "G" Badge
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF4285F4)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "G",
                  color = Color.White,
                  fontWeight = FontWeight.Black,
                  fontSize = 14.sp
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Text(
                text = "Masuk dengan Google",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF1F1F1F)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Zero-Trust Security indicator
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = Icons.Default.Lock,
          contentDescription = "Keamanan",
          tint = Color.White.copy(alpha = 0.5f),
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Terproteksi Firebase Auth & Firestore Rules",
          fontSize = 11.sp,
          color = Color.White.copy(alpha = 0.5f)
        )
      }
    }
  }
}

@Composable
private fun FeatureCard(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  subtitle: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
    color = CardBg
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.Start
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = accentColor,
        modifier = Modifier.size(22.dp)
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = title,
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = subtitle,
        color = TextSecondary,
        fontSize = 11.sp
      )
    }
  }
}
