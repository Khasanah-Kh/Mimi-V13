package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BadgeItem
import com.example.ui.components.CyberGradientButton
import com.example.ui.components.CyberProgressBar
import com.example.ui.components.GlowCard
import com.example.ui.components.MimiFoxMascot
import com.example.ui.theme.CardBg
import com.example.ui.theme.CyberGradient
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MimiUiState

/**
 * ⚔️ TANTANGAN SCREEN (1v1 SMP Arena)
 */
@Composable
fun TantanganScreen(
  uiState: MimiUiState,
  onChallengeRival: () -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(DarkNavyBg)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "ARENA TANTANGAN ⚔️",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp
          )
          Text(
            text = "Duel Cerdas Siswa SMP Se-Indonesia",
            color = Color(0xFF60A5FA),
            fontSize = 12.sp
          )
        }
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(PrimaryRed.copy(alpha = 0.2f))
            .border(1.dp, PrimaryRed, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text(
            text = "WIN STREAK 3x 🔥",
            color = PrimaryRed,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      }
    }

    // Active Rival Card
    item {
      GlowCard(
        modifier = Modifier.fillMaxWidth(),
        borderWidth = 1.5.dp,
        glowBrush = Brush.horizontalGradient(
          listOf(PrimaryRed, PrimaryBlue)
        )
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "RIVAL HARI INI",
              color = Color(0xFFF87171),
              fontWeight = FontWeight.Black,
              fontSize = 11.sp
            )
            Text(
              text = "Tingkat: Imbang ⚡",
              color = Color(0xFF93C5FD),
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Raka Avatar
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E293B))
                .border(1.5.dp, PrimaryBlue, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "🦊", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "${uiState.userName} (Kamu)",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = "SMP Kelas 8 • ${uiState.totalXp} XP",
                color = Color(0xFF60A5FA),
                fontSize = 11.sp
              )
            }

            Text(
              text = "VS",
              color = PrimaryRed,
              fontWeight = FontWeight.Black,
              fontSize = 18.sp,
              modifier = Modifier.padding(horizontal = 8.dp)
            )

            Column(
              modifier = Modifier.weight(1f),
              horizontalAlignment = Alignment.End
            ) {
              Text(
                text = "Arya P.",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = "SMPN 1 Bandung • 1.210 XP",
                color = TextSecondary,
                fontSize = 11.sp
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Rival Avatar
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E293B))
                .border(1.5.dp, PrimaryRed, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "🐯", fontSize = 24.sp)
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          CyberGradientButton(
            text = "TANTANG SEKARANG (+75 XP)",
            onClick = onChallengeRival,
            icon = "⚔️",
            modifier = Modifier.fillMaxWidth(),
            testTag = "challenge_rival_button"
          )
        }
      }
    }

    // Top Leaderboard Preview
    item {
      Text(
        text = "TOP SISWA SMP MINGGU INI",
        color = TextSecondary,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        letterSpacing = 0.5.sp
      )
    }

    val leaders = listOf(
      Triple("1", "Kenzo • SMP Labschool Jakarta", "2.840 XP"),
      Triple("2", "Nabila • SMPN 5 Surabaya", "2.610 XP"),
      Triple("3", "Raka (Kamu) • SMP Kelas 8", "${uiState.totalXp} XP"),
      Triple("4", "Farrel • SMP Al-Azhar 1", "1.180 XP")
    )

    items(leaders) { (rank, name, xp) ->
      val isMe = rank == "3"
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(if (isMe) Color(0xFF1E293B) else CardBg)
          .border(
            width = if (isMe) 1.5.dp else 1.dp,
            brush = if (isMe) CyberGradient else Brush.horizontalGradient(listOf(Color(0xFF334155), Color(0xFF1E293B))),
            shape = RoundedCornerShape(12.dp)
          )
          .padding(horizontal = 14.dp, vertical = 10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "#$rank",
              color = if (isMe) PrimaryBlue else Color(0xFF94A3B8),
              fontWeight = FontWeight.Black,
              fontSize = 15.sp,
              modifier = Modifier.width(36.dp)
            )
            Text(
              text = name,
              color = if (isMe) Color.White else TextPrimary,
              fontWeight = if (isMe) FontWeight.Bold else FontWeight.Medium,
              fontSize = 13.sp
            )
          }
          Text(
            text = xp,
            color = if (isMe) Color(0xFF6EE7B7) else Color(0xFF93C5FD),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp
          )
        }
      }
    }

    item { Spacer(modifier = Modifier.height(84.dp)) }
  }
}

/**
 * ❓ KUIS SCREEN (Speed Quiz 60s)
 */
@Composable
fun KuisScreen(
  uiState: MimiUiState,
  onAnswerSelected: (Int) -> Unit,
  onRestartQuiz: () -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(DarkNavyBg)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "KUIS KILAT SMP ❓",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp
          )
          Text(
            text = "Asah Otak Cepat • Bonus XP Melimpah",
            color = Color(0xFF60A5FA),
            fontSize = 12.sp
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, PrimaryBlue, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text(
            text = "SKOR: ${uiState.quizScore}/${uiState.quizQuestions.size}",
            color = Color(0xFF6EE7B7),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        }
      }
    }

    if (!uiState.quizFinished) {
      val question = uiState.quizQuestions.getOrNull(uiState.currentQuizIndex)
      if (question != null) {
        // Progress Header
        item {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Soal ${uiState.currentQuizIndex + 1} dari ${uiState.quizQuestions.size}",
                color = TextSecondary,
                fontSize = 12.sp
              )
              Text(
                text = "+50 XP Benar",
                color = Color(0xFFFBBF24),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            CyberProgressBar(
              progress = (uiState.currentQuizIndex + 1).toFloat() / uiState.quizQuestions.size.toFloat(),
              modifier = Modifier.fillMaxWidth(),
              height = 8.dp
            )
          }
        }

        // Question Card
        item {
          GlowCard(
            modifier = Modifier.fillMaxWidth(),
            borderWidth = 1.2.dp,
            glowBrush = Brush.horizontalGradient(listOf(PrimaryBlue, PrimaryRed))
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
            ) {
              Text(
                text = question.question,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 22.sp
              )
            }
          }
        }

        // Options
        items(question.options.indices.toList()) { index ->
          val optionText = question.options[index]
          val isSelected = uiState.selectedQuizAnswer == index
          val isAnswered = uiState.selectedQuizAnswer != null
          val isCorrect = index == question.correctIndex

          val (bgColor, borderColor) = when {
            isAnswered && isCorrect -> Pair(Color(0xFF064E3B), NeonGreen)
            isAnswered && isSelected && !isCorrect -> Pair(Color(0xFF450A0A), PrimaryRed)
            else -> Pair(CardBg, PrimaryBlue.copy(alpha = 0.35f))
          }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(bgColor)
              .border(1.2.dp, borderColor, RoundedCornerShape(12.dp))
              .clickable(
                enabled = !isAnswered,
                onClick = { onAnswerSelected(index) }
              )
              .padding(horizontal = 16.dp, vertical = 14.dp)
              .testTag("quiz_option_$index")
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = optionText,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
              )

              if (isAnswered && isCorrect) {
                Text(text = "✅ Benar!", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
              } else if (isAnswered && isSelected && !isCorrect) {
                Text(text = "❌ Kurang tepat", color = PrimaryRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
            }
          }
        }

        // Explanation if answered
        if (uiState.selectedQuizAnswer != null) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E293B))
                .padding(14.dp)
            ) {
              Column {
                Text(
                  text = "💡 Pembahasan MIMI:",
                  color = Color(0xFF60A5FA),
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = question.explanation,
                  color = TextPrimary,
                  fontSize = 13.sp,
                  lineHeight = 18.sp
                )
              }
            }
          }
        }
      }
    } else {
      // Quiz Finished Result
      item {
        GlowCard(
          modifier = Modifier.fillMaxWidth(),
          borderWidth = 1.5.dp,
          glowBrush = CyberGradient
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(text = "🎉", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Kuis Selesai, Raka!",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Kamu menjawab benar ${uiState.quizScore} dari ${uiState.quizQuestions.size} soal",
              color = Color(0xFF93C5FD),
              fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            CyberGradientButton(
              text = "ULANGI KUIS",
              onClick = onRestartQuiz,
              icon = "🔄",
              modifier = Modifier.fillMaxWidth(),
              testTag = "restart_quiz_button"
            )
          }
        }
      }
    }

    item { Spacer(modifier = Modifier.height(84.dp)) }
  }
}

/**
 * 🏆 LENCANA SCREEN
 */
@Composable
fun LencanaScreen(
  badges: List<BadgeItem>,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(DarkNavyBg)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "LENCANA PRESTASI 🏆",
        color = Color.White,
        fontWeight = FontWeight.Black,
        fontSize = 20.sp
      )
      Text(
        text = "Koleksi piala gamer belajar Raka",
        color = Color(0xFF60A5FA),
        fontSize = 12.sp
      )
    }

    items(badges) { badge ->
      val alpha = if (badge.unlocked) 1f else 0.4f

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .alpha(alpha)
          .clip(RoundedCornerShape(14.dp))
          .background(CardBg)
          .border(
            width = if (badge.unlocked) 1.5.dp else 1.dp,
            brush = if (badge.unlocked) CyberGradient else Brush.horizontalGradient(listOf(Color(0xFF334155), Color(0xFF1E293B))),
            shape = RoundedCornerShape(14.dp)
          )
          .padding(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(52.dp)
              .clip(CircleShape)
              .background(if (badge.unlocked) Color(0xFF1E293B) else Color(0xFF0F172A))
              .border(
                1.5.dp,
                if (badge.unlocked) PrimaryBlue else Color(0xFF475569),
                CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(text = badge.emoji, fontSize = 26.sp)
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = badge.title,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = badge.desc,
              color = TextSecondary,
              fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = badge.date,
              color = if (badge.unlocked) NeonGreen else Color(0xFF64748B),
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }
    }

    item { Spacer(modifier = Modifier.height(84.dp)) }
  }
}

/**
 * 👤 PROFIL SCREEN
 */
@Composable
fun ProfilScreen(
  uiState: MimiUiState,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(DarkNavyBg)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "PROFIL BELAJAR 👤",
        color = Color.White,
        fontWeight = FontWeight.Black,
        fontSize = 20.sp
      )
    }

    // Profile Card
    item {
      GlowCard(
        modifier = Modifier.fillMaxWidth(),
        borderWidth = 1.5.dp,
        glowBrush = CyberGradient
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(72.dp)
              .clip(CircleShape)
              .background(CardBg)
              .border(2.dp, CyberGradient, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            MimiFoxMascot(size = 56.dp, showPulse = false)
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "${uiState.userName} • Cyber Explorer",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = uiState.userSubtitle,
            color = Color(0xFF60A5FA),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Stat boxes
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            StatBox(label = "Streak", value = "${uiState.streakDays} Hari 🔥", modifier = Modifier.weight(1f))
            StatBox(label = "Total XP", value = "${uiState.totalXp} ⚡", modifier = Modifier.weight(1f))
            StatBox(label = "Win Rate", value = "88% ⚔️", modifier = Modifier.weight(1f))
          }
        }
      }
    }

    // Settings & Target
    item {
      Text(
        text = "PREFERENSI BELAJAR",
        color = TextSecondary,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
      )
    }

    val settingsItems = listOf(
      "Target Harian: 3 Sesi Belajar / Hari 🎯",
      "Notifikasi Pengingat Streak Belajar 🔔",
      "Efek Suara & Haptik Cyber Gamer 🎮",
      "MIMI V13 AI Guru Asik IPA & Matematika ✨"
    )

    items(settingsItems) { itemText ->
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(CardBg)
          .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
          .padding(horizontal = 16.dp, vertical = 14.dp)
      ) {
        Text(
          text = itemText,
          color = TextPrimary,
          fontSize = 13.5.sp,
          fontWeight = FontWeight.Medium
        )
      }
    }

    item { Spacer(modifier = Modifier.height(84.dp)) }
  }
}

@Composable
private fun StatBox(
  label: String,
  value: String,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(Color(0xFF0F172A))
      .border(1.dp, PrimaryBlue.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
      .padding(vertical = 10.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(text = value, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = label, color = TextSecondary, fontSize = 10.sp)
    }
  }
}
